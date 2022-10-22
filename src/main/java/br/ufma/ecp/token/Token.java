package br.ufma.ecp.token;
public class Token {

    public final TokenType type;
    public String lexeme;


    public Token (TokenType type, String lexeme) {
        this.type = type;
        this.lexeme = lexeme;
    }

//    public String toString() {
//        var type = this.type.toString();
//
//        if (TokenType.isSymbol(lexeme.charAt(0))){
//            type = "symbol";
//            lexeme = TokenType.getSymbol(lexeme.charAt(0));
//        } else {
//            type = this.type.getTipo();
//        }
//
//        return "<"+ type +"> " + lexeme + " </"+ type + ">";
//    }
//

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
        } else if (TokenType.getSymbol(lexeme.charAt(0)) != null) {
            categoria = "keyword";
        } else if (categoria.equals("string")) {
            categoria = "stringConstant";
        }
        return "<" + categoria + "> " + valor  + " </" + categoria + ">";
    }
}
