public class Lexeme {
    private Token token;
    private String value;
    private int lineNumber;
    private int columnNumber;

    public Lexeme(Token token, String value, int lineNumber, int columnNumber) {
        this.token = token;
        this.value = value;
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
    }

    // Getters and setters for the fields

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public int getColumnNumber() {
        return columnNumber;
    }

    public void setColumnNumber(int columnNumber) {
        this.columnNumber = columnNumber;
    }
}
