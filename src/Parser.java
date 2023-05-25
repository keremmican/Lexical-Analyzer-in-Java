import java.util.List;

public class Parser {

    private List<Lexeme> lexemes;
    private int currentIndex;

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
        program();
    }

    private void program() throws ParserException {
        while (getCurrentLexeme() != null) {
            topLevelForm();
        }
    }

    private void topLevelForm() throws ParserException {
        match(Token.LEFTPAR);
        secondLevelForm();
        match(Token.RIGHTPAR);
    }

    private void secondLevelForm() throws ParserException {
        if (getCurrentLexeme().getToken() == Token.DEFINE) {
            definition();
        } else if (getCurrentLexeme().getToken() == Token.LEFTPAR) {
            funCall();
        } else {
            throw new ParserException("Syntax Error: expected DEFINE or ( but found " + getCurrentLexeme().getToken().name());
        }
    }

    private void definition() throws ParserException {
        match(Token.DEFINE);
        definitionRight();
    }

    private void definitionRight() throws ParserException {
        if (getCurrentLexeme().getToken() == Token.IDENTIFIER) {
            getNextLexeme();
            expression();
        } else if (getCurrentLexeme().getToken() == Token.LEFTPAR) {
            match(Token.LEFTPAR);
            match(Token.IDENTIFIER);
            argList();
            match(Token.RIGHTPAR);
            statements();
        } else {
            throw new ParserException("Syntax Error: expected IDENTIFIER or ( but found " + getCurrentLexeme().getToken().name());
        }
    }

    private void argList() throws ParserException {
        if (getCurrentLexeme().getToken() == Token.IDENTIFIER) {
            getNextLexeme();
            argList();
        } else if (getCurrentLexeme().getToken() != Token.RIGHTPAR) {
            throw new ParserException("Syntax Error: expected IDENTIFIER or ) but found " + getCurrentLexeme().getToken().name());
        }
    }

    private void statements() throws ParserException {
        while (getCurrentLexeme().getToken() != Token.RIGHTPAR) {
            if (getCurrentLexeme().getToken() == Token.LEFTPAR) {
                getNextLexeme();
                if (getCurrentLexeme().getToken() == Token.DEFINE) {
                    definition();
                } else {
                    expr();
                    match(Token.RIGHTPAR);
                }
            } else {
                throw new ParserException("Syntax Error: unexpected token " + getCurrentLexeme().getToken().name());
            }
        }
    }

    private void expression() throws ParserException {
        if (getCurrentLexeme().getToken() == Token.IDENTIFIER || getCurrentLexeme().getToken().isLiteral()) {
            getNextLexeme();
        } else if (getCurrentLexeme().getToken() == Token.LEFTPAR) {
            getNextLexeme();
            expr();
            match(Token.RIGHTPAR);
        } else {
            throw new ParserException("Syntax Error: unexpected token " + getCurrentLexeme().getToken().name());
        }
    }

    private void expr() throws ParserException {
        if (getCurrentLexeme().getToken() == Token.LET) {
            letExpression();
        } else if (getCurrentLexeme().getToken() == Token.COND) {
            condExpression();
        } else if (getCurrentLexeme().getToken() == Token.IF) {
            ifExpression();
        } else if (getCurrentLexeme().getToken() == Token.BEGIN) {
            beginExpression();
        } else if (getCurrentLexeme().getToken() == Token.IDENTIFIER) {
            funCall();
        } else {
            throw new ParserException("Syntax Error: unexpected token " + getCurrentLexeme().getToken().name());
        }
    }

    private void funCall() throws ParserException {
        match(Token.IDENTIFIER);
        expressions();
    }

    private void expressions() throws ParserException {
        while (getCurrentLexeme() != null && (getCurrentLexeme().getToken() == Token.LEFTPAR || getCurrentLexeme().getToken().isLiteral())) {
            expression();
        }
    }

    private void letExpression() throws ParserException {
        match(Token.LET);
        letExpr();
    }

    private void letExpr() throws ParserException {
        if (getCurrentLexeme().getToken() == Token.LEFTPAR) {
            match(Token.LEFTPAR);
            varDefs();
            match(Token.RIGHTPAR);
            statements();
        } else if (getCurrentLexeme().getToken() == Token.IDENTIFIER) {
            match(Token.IDENTIFIER);
            match(Token.LEFTPAR);
            varDefs();
            match(Token.RIGHTPAR);
            statements();
        } else {
            throw new ParserException("Syntax Error: expected ( or IDENTIFIER but found " + getCurrentLexeme().getToken().name());
        }
    }

    private void varDefs() throws ParserException {
        if (getCurrentLexeme().getToken() == Token.LEFTPAR) {
            match(Token.LEFTPAR);
            match(Token.IDENTIFIER);
            expression();
            match(Token.RIGHTPAR);
            varDef();
        } else if (getCurrentLexeme().getToken() != Token.RIGHTPAR) {
            throw new ParserException("Syntax Error: expected ( or ) but found " + getCurrentLexeme().getToken().name());
        }
    }

    private void varDef() throws ParserException {
        if (getCurrentLexeme().getToken() == Token.LEFTPAR) {
            varDefs();
        } else if (getCurrentLexeme().getToken() != Token.RIGHTPAR) {
            throw new ParserException("Syntax Error: expected ( or ) but found " + getCurrentLexeme().getToken().name());
        }
    }

    private void condExpression() throws ParserException {
        match(Token.COND);
        condBranches();
    }

    private void condBranches() throws ParserException {
        if (getCurrentLexeme().getToken() == Token.LEFTPAR) {
            match(Token.LEFTPAR);
            expression();
            statements();
            match(Token.RIGHTPAR);
            condBranches();
        } else if (getCurrentLexeme().getToken() != Token.RIGHTPAR) {
            throw new ParserException("Syntax Error: expected ( or ) but found " + getCurrentLexeme().getToken().name());
        }
    }

    private void ifExpression() throws ParserException {
        match(Token.IF);
        expression();
        expression();
        endExpression();
        match(Token.RIGHTPAR);
    }

    private void endExpression() throws ParserException {
        if (getCurrentLexeme().getToken() != Token.RIGHTPAR) {
            expression();
        }
    }

    private void beginExpression() throws ParserException {
        match(Token.BEGIN);
        statements();
    }

    class ParserException extends Exception {
        public ParserException(String message) {
            super(message);
        }
    }
}
