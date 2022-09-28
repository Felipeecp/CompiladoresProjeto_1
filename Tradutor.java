public class Tradutor {
    public static void main(String[] args) {
        String input = "let a = 42 + 5 - 8;";
        Parser p = new Parser (input.getBytes());
        p.parse();
    }
}