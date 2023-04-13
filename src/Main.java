import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {

        String filePath = "src/input.txt";  // get the file path
        Map<Integer, String> input = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {  // read the file
            String line;
            int lineNumber = 1;

            while ((line = reader.readLine()) != null) {
                input.put(lineNumber, line);
                lineNumber++;
            }
        } catch (IOException e) {
            System.err.println("Error reading the input file: " + e.getMessage());
            return;
        }

        Lexical_Analyzer analyzer = new Lexical_Analyzer(); // create analyzer object
        List<Lexeme> lexemes = analyzer.analyzeCode(input); // start analyzing the input

        for (Lexeme lexeme : lexemes) { // Print the lexemes
            System.out.printf("%s %d:%d\n", lexeme.getToken().name(), lexeme.getLineNumber(), lexeme.getColumnNumber());
        }
    }
}