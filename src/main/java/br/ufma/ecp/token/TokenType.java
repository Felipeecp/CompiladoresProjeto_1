package br.ufma.ecp.token;

import java.util.List;
import java.util.Map;

public enum TokenType {
    PLUS,MINUS,

     // Literals.
     NUMBER,
     STRING,


     IDENT,

 
     // keywords
     METHOD,
     WHILE,
     IF,
     CLASS,
     CONSTRUCTOR,

     EOF,
    LET,
    EQ,
    SEMICOLON,
     ILLEGAL,
    PRINT;

     static public boolean isSymbol (char c) {
        String symbols = "{}()[].,;+-*/&|<>=~";
        return symbols.indexOf(c) > -1;
    }


    static public boolean isKeyword (TokenType type) {
        List<TokenType> keywords  = 
            List.of(
                METHOD,
                WHILE,
                IF,
                CLASS,
                CONSTRUCTOR,
                PRINT
            );
            return keywords.contains(type);
    }

}
