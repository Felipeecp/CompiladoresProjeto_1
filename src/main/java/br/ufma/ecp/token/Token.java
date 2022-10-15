package br.ufma.ecp.token;
public class Token {

    public final TokenType type;
    public String lexeme;


    public Token (TokenType type, String lexeme) {
        this.type = type;
        this.lexeme = lexeme;
    }

    public String toString() {
        var type = this.type.toString();

        if (TokenType.isSymbol(lexeme.charAt(0))){
            type = "symbol";
            lexeme = TokenType.getSymbol(lexeme.charAt(0));
        } else {
            type = this.type.getTipo();
        }

        return "<"+ type +"> " + lexeme + " </"+ type + ">";
    }
    
}
