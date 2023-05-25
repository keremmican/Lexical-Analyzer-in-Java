import java.util.List;

public class Parser {

    private List<Lexeme> lexemes;
    private int currentIndex;
    private Node ast;

    public Parser(List<Lexeme> lexemes) {
        this.lexemes = lexemes;
        this.currentIndex = 0;
    }

    private Lexeme getCurrentLexeme() {
        return currentIndex < lexemes.size() ? lexemes.get(currentIndex) : null;
    }

    private Lexeme getNextLexeme() {
        currentIndex++;
        return getCurrentLexeme();
    }

    private void match(Token expectedToken) throws ParserException {
        Lexeme current = getCurrentLexeme();
        if (current != null && current.getToken() == expectedToken) {
            getNextLexeme();
        } else {
            throw new ParserException("Syntax Error: expected " + expectedToken.name() + " but found " + (current != null ? current.getToken().name() : "EOF"));
        }
    }

    public void parse() throws ParserException {
        ast = program();
        if (getCurrentLexeme() != null) {
            throw new ParserException("Syntax Error: unexpected token " + getCurrentLexeme().getToken().name());
        }
    }

    public Node getAST() {
        return ast;
    }

    private Node program() throws ParserException {
        Node programNode = new Node("Program");

        while (getCurrentLexeme() != null) {
            programNode.addChild(topLevelForm());
        }

        return programNode;
    }

    private Node topLevelForm() throws ParserException {
        match(Token.LEFTPAR);
        Node topLevelFormNode = new Node("TopLevelForm");
        topLevelFormNode.addChild(secondLevelForm());
        match(Token.RIGHTPAR);
        return topLevelFormNode;
    }

    private Node secondLevelForm() throws ParserException {
        Lexeme current = getCurrentLexeme();
        if (current != null && current.getToken() == Token.DEFINE) {
            return definition();
        } else if (current != null && current.getToken() == Token.LEFTPAR) {
            return funCall();
        } else {
            throw new ParserException("Syntax Error: expected DEFINE or ( but found " + (current != null ? current.getToken().name() : "EOF"));
        }
    }

    private Node definition() throws ParserException {
        match(Token.DEFINE);
        return definitionRight();
    }

    private Node definitionRight() throws ParserException {
        Lexeme current = getCurrentLexeme();
        if (current != null && current.getToken() == Token.IDENTIFIER) {
            Node identifierNode = new Node("Identifier", current.getValue());
            getNextLexeme();
            Node expressionNode = expression();
            Node definitionRightNode = new Node("DefinitionRight");
            definitionRightNode.addChild(identifierNode);
            definitionRightNode.addChild(expressionNode);
            return definitionRightNode;
        } else if (current != null && current.getToken() == Token.LEFTPAR) {
            match(Token.LEFTPAR);
            match(Token.IDENTIFIER);
            Node argListNode = argList();
            match(Token.RIGHTPAR);
            Node statementsNode = statements();
            Node definitionRightNode = new Node("DefinitionRight");
            definitionRightNode.addChild(argListNode);
            definitionRightNode.addChild(statementsNode);
            return definitionRightNode;
        } else {
            throw new ParserException("Syntax Error: expected IDENTIFIER or ( but found " + (current != null ? current.getToken().name() : "EOF"));
        }
    }

    private Node argList() throws ParserException {
        Lexeme current = getCurrentLexeme();
        if (current != null && current.getToken() == Token.IDENTIFIER) {
            Node identifierNode = new Node("Identifier", current.getValue());
            getNextLexeme();
            Node argListNode = argList();
            argListNode.addChild(identifierNode);
            return argListNode;
        } else if (current != null && current.getToken() != Token.RIGHTPAR) {
            throw new ParserException("Syntax Error: expected IDENTIFIER or ) but found " + (current != null ? current.getToken().name() : "EOF"));
        } else {
            return new Node("ArgList");
        }
    }

    private Node statements() throws ParserException {
        Node statementsNode = new Node("Statements");

        while (getCurrentLexeme() != null && getCurrentLexeme().getToken() != Token.RIGHTPAR) {
            statementsNode.addChild(expression());
        }

        return statementsNode;
    }

    private Node expression() throws ParserException {
        Lexeme current = getCurrentLexeme();
        if (current != null && (current.getToken().isLiteral() || current.getToken() == Token.IDENTIFIER)) {
            Node literalNode = new Node("Literal", current.getValue());
            getNextLexeme();
            return literalNode;
        } else if (current != null && current.getToken() == Token.LEFTPAR) {
            match(Token.LEFTPAR);
            Node exprNode = expr();
            match(Token.RIGHTPAR);
            return exprNode;
        } else {
            throw new ParserException("Syntax Error: unexpected token " + (current != null ? current.getToken().name() : "EOF"));
        }
    }

    private Node expr() throws ParserException {
        Lexeme current = getCurrentLexeme();
        if (current != null && current.getToken() == Token.LET) {
            return letExpression();
        } else if (current != null && current.getToken() == Token.COND) {
            return condExpression();
        } else if (current != null && current.getToken() == Token.IF) {
            return ifExpression();
        } else if (current != null && current.getToken() == Token.BEGIN) {
            return beginExpression();
        } else if (current != null && current.getToken() == Token.IDENTIFIER) {
            return funCall();
        } else {
            throw new ParserException("Syntax Error: unexpected token " + (current != null ? current.getToken().name() : "EOF"));
        }
    }

    private Node funCall() throws ParserException {
        match(Token.IDENTIFIER);
        Node funCallNode = new Node("FunCall");
        funCallNode.addChild(new Node("Identifier", getCurrentLexeme().getValue()));
        expressions(funCallNode);
        return funCallNode;
    }

    private void expressions(Node parentNode) throws ParserException {
        if (getCurrentLexeme() != null && (getCurrentLexeme().getToken().isLiteral() || getCurrentLexeme().getToken() == Token.IDENTIFIER || getCurrentLexeme().getToken() == Token.LEFTPAR)) {
            parentNode.addChild(expression());
            expressions(parentNode);
        }
    }

    private Node letExpression() throws ParserException {
        match(Token.LET);
        return letExpr();
    }

    private Node letExpr() throws ParserException {
        Lexeme current = getCurrentLexeme();
        if (current != null && current.getToken() == Token.LEFTPAR) {
            match(Token.LEFTPAR);
            Node varDefsNode = varDefs();
            match(Token.RIGHTPAR);
            Node statementsNode = statements();
            Node letExprNode = new Node("LetExpr");
            letExprNode.addChild(varDefsNode);
            letExprNode.addChild(statementsNode);
            return letExprNode;
        } else if (current != null && current.getToken() == Token.IDENTIFIER) {
            match(Token.IDENTIFIER);
            match(Token.LEFTPAR);
            Node varDefsNode = varDefs();
            match(Token.RIGHTPAR);
            Node statementsNode = statements();
            Node letExprNode = new Node("LetExpr");
            letExprNode.addChild(new Node("Identifier", current.getValue()));
            letExprNode.addChild(varDefsNode);
            letExprNode.addChild(statementsNode);
            return letExprNode;
        } else {
            throw new ParserException("Syntax Error: expected ( or IDENTIFIER but found " + (current != null ? current.getToken().name() : "EOF"));
        }
    }

    private Node varDefs() throws ParserException {
        Lexeme current = getCurrentLexeme();
        if (current != null && current.getToken() == Token.LEFTPAR) {
            match(Token.LEFTPAR);
            match(Token.IDENTIFIER);
            Node expressionNode = expression();
            match(Token.RIGHTPAR);
            Node varDefsNode = varDef();
            varDefsNode.addChild(expressionNode);
            return varDefsNode;
        } else if (current != null && current.getToken() != Token.RIGHTPAR) {
            throw new ParserException("Syntax Error: expected ( or ) but found " + (current != null ? current.getToken().name() : "EOF"));
        } else {
            return new Node("VarDefs");
        }
    }

    private Node varDef() throws ParserException {
        Lexeme current = getCurrentLexeme();
        if (current != null && current.getToken() == Token.LEFTPAR) {
            Node varDefsNode = varDefs();
            return varDefsNode;
        } else if (current != null && current.getToken() != Token.RIGHTPAR) {
            throw new ParserException("Syntax Error: expected ( or ) but found " + (current != null ? current.getToken().name() : "EOF"));
        } else {
            return new Node("VarDef");
        }
    }

    private Node condExpression() throws ParserException {
        match(Token.COND);
        return condBranches();
    }

    private Node condBranches() throws ParserException {
        Lexeme current = getCurrentLexeme();
        if (current != null && current.getToken() == Token.LEFTPAR) {
            match(Token.LEFTPAR);
            Node expressionNode = expression();
            Node statementsNode = statements();
            match(Token.RIGHTPAR);
            Node condBranchesNode = condBranches();
            Node condBranchNode = new Node("CondBranch");
            condBranchNode.addChild(expressionNode);
            condBranchNode.addChild(statementsNode);
            condBranchNode.addChild(condBranchesNode);
            return condBranchNode;
        } else if (current != null && current.getToken() != Token.RIGHTPAR) {
            throw new ParserException("Syntax Error: expected ( or ) but found " + (current != null ? current.getToken().name() : "EOF"));
        } else {
            return new Node("CondBranches");
        }
    }

    private Node ifExpression() throws ParserException {
        match(Token.IF);
        Node ifExpressionNode = new Node("IfExpression");
        ifExpressionNode.addChild(expression());
        ifExpressionNode.addChild(expression());
        if (getCurrentLexeme() != null && getCurrentLexeme().getToken() != Token.RIGHTPAR) {
            ifExpressionNode.addChild(expression());
        }
        return ifExpressionNode;
    }

    private Node beginExpression() throws ParserException {
        match(Token.BEGIN);
        Node beginExpressionNode = new Node("BeginExpression");
        beginExpressionNode.addChild(statements());
        return beginExpressionNode;
    }

    public static class ParserException extends Exception {
        public ParserException(String message) {
            super(message);
        }
    }
}
