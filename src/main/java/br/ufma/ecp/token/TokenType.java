package br.ufma.ecp.token;

import java.util.List;
import java.util.Map;

public enum TokenType {
    PLUS("operacao", "+"),
    MINUS("operacao", "-"),

     // Literals.
    NUMBER("integerConstant", "valueNumber"),
    STRING("stringConst", "valueString"),
    IDENTIFIER("identifier", "sequenceOfLeters"),

     // keyword
    CLASS("keyword", "class"),
    CONSTRUCTOR("keyword","constructor"),
    FUNCTION("keyword", "function"),
    METHOD("keyword", "method"),
    FIELD("keyword", "field"),
    STATIC("keyword", "static"),
    VAR("keyword", "var"),
    INT("keyword", "int"),
    CHAR("keyword", "char"),
    BOOLEAN("keyword", "boolean"),
    VOID("keyword", "void"),
    TRUE("keyword", "true"),
    FALSE("keyword", "false"),
    NULL("keyword", "null"),
    THIS("keyword", "this"),
    LET("keyword", "let"),
    DO("keyword", "do"),
    IF("keyword", "if"),
    ELSE("keyword", "else"),
    WHILE("keyword", "while"),
    RETURN("keyword", "return"),
    EOF("system", "fim"),

    ILLEGAL("system", "illegal"),
    SYMBOL("symbol", "symbolCharacter");

    public final String tipo;
    public final String lexeme;

    public String getTipo(){
        return tipo;
    }

    TokenType(String tipo, String lexeme) {
        this.tipo = tipo;
        this.lexeme = lexeme;
    }

     static public boolean isSymbol (char c) {
        String symbols = "{}()[].,;+-*/&|<>=~";
        return symbols.indexOf(c) > -1;
    }

    static public String getSymbol(char c){
        switch (c){
            case '<' -> {
                return "&lt;";
            }
            case '>' -> {
                return "&gt;";
            }
            case '&' -> {
                return "&amp;";
            }
            default -> {
                return String.valueOf(c);
            }
        }
    }

    public static TokenType valueOfLexeme(String lexeme) {
        for (TokenType e : values()) {
            if (e.lexeme.equals(lexeme)) {
                return e;
            }
        }
        return null;
    }


}
