package br.ufma.ecp.parser;

import br.ufma.ecp.Parser;

import static br.ufma.ecp.token.TokenType.*;
import static br.ufma.ecp.token.TokenType.SEMICOLON;

public class ClassVardec extends SyntaxyParser{

    public ClassVardec(Parser parser, StringBuilder xmlOutput) {
        super(parser, xmlOutput);
    }

    public void parseClassVarDec () {
        while (getPeekTokenType() == FIELD || getPeekTokenType() == STATIC) {
            printNonTerminal("classVarDec");
            expectPeek(FIELD, STATIC);
            expectPeek(INT, CHAR, BOOLEAN, IDENTIFIER);
            expectPeek(IDENTIFIER);

            switch (getPeekTokenType()) {
                case COMMA -> {
                    while (getPeekTokenType() == COMMA) {
                        expectPeek(COMMA);
                        expectPeek(IDENTIFIER);
                    }
                }
                case EQ -> {
                    expectPeek(EQ);
                    expectPeek(NUMBER, STRING, BOOLEAN);
                }
            }

            expectPeek(SEMICOLON);
            printNonTerminal("/classVarDec");
        }
    }

}
