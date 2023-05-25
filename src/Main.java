import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        String filePath = "src/input.txt";

        try {
            List<String> lines = readFile(filePath);

            Map<Integer, String> input = createInputMap(lines);

            Lexical_Analyzer lexicalAnalyzer = new Lexical_Analyzer();

            List<Lexeme> lexemes = lexicalAnalyzer.analyzeCode(input);

            Parser parser = new Parser(lexemes);

            try {
                parser.parse();
                System.out.println("Parsing completed successfully.");
            } catch (Parser.ParserException e) {
                System.err.println("Parsing Error: " + e.getMessage());
            }
        } catch (IOException e) {
            System.err.println("Error reading the input file: " + e.getMessage());
        }
    }

    private static List<String> readFile(String filePath) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }

    private static Map<Integer, String> createInputMap(List<String> lines) {
        Map<Integer, String> input = new HashMap<>();
        for (int i = 0; i < lines.size(); i++) {
            input.put(i + 1, lines.get(i));
        }
        return input;
    }
}
