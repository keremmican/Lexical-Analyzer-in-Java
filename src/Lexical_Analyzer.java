import java.util.*;

public class Lexical_Analyzer {
    private Map<String, Token> keywordsAndOperatorsMap;

    public Lexical_Analyzer() {  // define the keywords and operators
        this.keywordsAndOperatorsMap = new HashMap<>();
        keywordsAndOperatorsMap.put("(", Token.LEFTPAR);
        keywordsAndOperatorsMap.put(")", Token.RIGHTPAR);
        keywordsAndOperatorsMap.put("[", Token.LEFTSQUAREB);
        keywordsAndOperatorsMap.put("]", Token.RIGHTSQUAREB);
        keywordsAndOperatorsMap.put("{", Token.LEFTCURLYB);
        keywordsAndOperatorsMap.put("}", Token.RIGHTCURLYB);
        keywordsAndOperatorsMap.put("define", Token.DEFINE);
        keywordsAndOperatorsMap.put("let", Token.LET);
        keywordsAndOperatorsMap.put("cond", Token.COND);
        keywordsAndOperatorsMap.put("if", Token.IF);
        keywordsAndOperatorsMap.put("begin", Token.BEGIN);
    }

    public List<Lexeme> analyzeCode(Map<Integer,String> lines) {
        List<Lexeme> lexemes = new ArrayList<>();
        try {
            for (Map.Entry<Integer, String> entry : lines.entrySet()) { // analyze line by line and get the lexemes
                int nLine = entry.getKey();
                String line = entry.getValue();
                Map<Integer, Lexeme> lexLine = analyzeLine(nLine, line.strip());
                lexemes.addAll(lexLine.values());
            }
        } catch (LexicalException e) { // if invalid token found, throw exception
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return lexemes;
    }
    public Map<Integer, Lexeme> analyzeLine(int lineNumber, String line) throws LexicalException {
        Automaton automaton = new Automaton();
        Map<Integer, Lexeme> lineTokens = new TreeMap<>();
        StringBuilder currentLexeme = new StringBuilder();
        StringBuilder invalidLexeme = new StringBuilder();
        int columnNumber = 1;
        int invalidTokenColumnNumber = columnNumber;

        boolean invalidTokenFlag = false;
        for (char c : line.toCharArray()) { // read line char by char
            Token token = automaton.evaluate(c); // match the character with token
            if (token == Token.NONE) {
                currentLexeme.append(c);
            } else { // process tokens and create current lexeme
                if (currentLexeme.length() > 0) {
                    String lexemeString = currentLexeme.toString().strip();
                    if (!lexemeString.isEmpty()) {
                        Token prevToken = Token.NONE;
                        if (Character.isLetter(lexemeString.charAt(0))) {
                            prevToken = automaton.processIdentifier(lexemeString);
                        } else if (Character.isDigit(lexemeString.charAt(0))) {
                            prevToken = Token.NUMBER;
                        }
                        if (prevToken != Token.NONE) {
                            lineTokens.put(columnNumber - lexemeString.length(), new Lexeme(prevToken, lexemeString, lineNumber, columnNumber - lexemeString.length()));
                        }
                    }
                    currentLexeme.setLength(0);
                }
                if (token != Token.IDENTIFIER && token != Token.NUMBER) {
                    if (token == Token.INVALID) {
                        invalidTokenFlag = true;
                        invalidLexeme.append(c);

                        if (invalidLexeme.length() > 0 && Character.isDigit(invalidLexeme.charAt(0))) {
                            token = Token.NUMBER;
                        }
                        while (columnNumber < line.length() && !Character.isWhitespace(line.charAt(columnNumber)) && (automaton.evaluate(line.charAt(columnNumber)) == Token.NONE || token == Token.NUMBER)) {
                            invalidLexeme.append(line.charAt(columnNumber));
                            columnNumber++;
                        }
                        invalidTokenColumnNumber = columnNumber;
                    } else {
                        lineTokens.put(columnNumber, new Lexeme(token, String.valueOf(c), lineNumber, columnNumber));
                    }
                } else {
                    currentLexeme.append(c);
                }
            }
            columnNumber++;
        }

        if (invalidTokenFlag) { // if inavlid token found, stop processing and throw an exception
            throw new LexicalException(String.format("LEXICAL ERROR [%d:%d]: Invalid token `%s'", lineNumber, invalidTokenColumnNumber, invalidLexeme));        }

        if (currentLexeme.length() > 0) {
            String lexemeString = currentLexeme.toString().strip();
            if (!lexemeString.isEmpty()) {
                Token lastToken = Token.NONE;
                if (Character.isLetter(lexemeString.charAt(0))) {
                    lastToken = automaton.processIdentifier(lexemeString);
                } else if (Character.isDigit(lexemeString.charAt(0))) {
                    lastToken = Token.NUMBER;
                }
                if (lastToken != Token.NONE) { // create lexeme for the output
                    lineTokens.put(columnNumber - lexemeString.length(), new Lexeme(lastToken, lexemeString, lineNumber, columnNumber - lexemeString.length()));
                }
            }
        }
        return lineTokens;
    }
}