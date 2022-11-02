package br.ufma.ecp.token;
public class Token {

    public final TokenType type;
    public String lexeme;


    public Token (TokenType type, String lexeme) {
        this.type = type;
        this.lexeme = lexeme;
    }

    public String toString() {
        String categoria = type.toString().toLowerCase();

        String valor = lexeme;
        if (TokenType.isSymbol(lexeme.charAt(0))) {
            categoria = "symbol";
            //Os símbolos <, >, ", e & são impressos como &lt;  &gt;  &quot; e &amp; Para não conflitar com o significado destes símbolos no XML
            switch (valor) {
                case ">" -> valor = "&gt;";
                case "<" -> valor = "&lt;";
                case "\"" -> valor = "&quot;";
                case "&" -> valor = "&amp;";
            }

        } else if (categoria.equals("number")) {
            categoria = "integerConstant";
        } else {
            categoria = "keyword";
        }
        return "<" + categoria + "> " + valor  + " </" + categoria + ">";
    }
}
