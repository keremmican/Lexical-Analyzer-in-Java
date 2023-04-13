public class Automaton {
    private State state;

    private enum State {  // define automaton states
        START,
        IDENTIFIER,
        NUMBER,
        CHAR_LITERAL,
        STRING_LITERAL,
        COMMENT,
        INVALID
    }

    public Automaton() {
        this.state = State.START;
    }
    Token processIdentifier(String identifier) {
        switch (identifier) {
            case "define":
                return Token.DEFINE;
            case "let":
                return Token.LET;
            case "if":
                return Token.IF;
            default:
                return Token.IDENTIFIER;
         }
        }

    public Token evaluate(char input) {
        Token result = Token.NONE;

        switch (state) {
            case START:
                if (input == '(') {
                    result = Token.LEFTPAR;
                } else if (input == ')') {
                    result = Token.RIGHTPAR;
                } else if (input == '[') {
                    result = Token.LEFTSQUAREB;
                } else if (input == ']') {
                    result = Token.RIGHTSQUAREB;
                } else if (Character.isLetter(input)) {
                    state = State.IDENTIFIER;
                    result = Token.IDENTIFIER;
                } else if (Character.isDigit(input)) {
                    state = State.NUMBER;
                    result = Token.NUMBER;
                }
                break;
            case IDENTIFIER:
                if (Character.isWhitespace(input) || input == '(' || input == ')' || input == '[' || input == ']') {
                    state = State.START;
                    result = evaluate(input);
                } else if (Character.isDigit(input)) {
                    state = State.INVALID;
                    result = Token.INVALID;
                }
                break;
            case NUMBER:
                if (Character.isWhitespace(input) || input == '(' || input == ')' || input == '[' || input == ']') {
                    state = State.START;
                    result = evaluate(input);
                } else if (Character.isLetter(input)) {
                    state = State.INVALID;
                    result = Token.INVALID;
                }
                break;
            case INVALID:
                if (Character.isWhitespace(input) || input == '(' || input == ')' || input == '[' || input == ']') {
                    state = State.START;
                    result = evaluate(input);
                } else {
                    result = Token.INVALID;
                }
                break;
            default:
                break;
        }
        return result;
    }
}
