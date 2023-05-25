public enum Token {
    LEFTPAR, RIGHTPAR, LEFTSQUAREB, RIGHTSQUAREB, LEFTCURLYB, RIGHTCURLYB,
    NUMBER, BOOLEAN, CHAR, STRING,
    DEFINE, LET, COND, IF, BEGIN,
    IDENTIFIER, NONE, INVALID;

    public boolean isLiteral() {
        return this == NUMBER || this == BOOLEAN || this == CHAR || this == STRING;
    }
}

