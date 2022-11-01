package br.ufma.ecp.parser;

import br.ufma.ecp.Parser;
import br.ufma.ecp.VMWriter;

import static br.ufma.ecp.token.TokenType.*;
import static br.ufma.ecp.token.TokenType.SEMICOLON;

public class ClassVardec extends SyntaxyParser{

    public ClassVardec(Parser parser, StringBuilder xmlOutput, VMWriter vmWriter) {
        super(parser, xmlOutput, vmWriter);
    }

    public void parseClassVarDec (){
        while (getPeekToken().type == FIELD || getPeekToken().type == STATIC) {
            printNonTerminal("classVarDec");
            expectPeek(FIELD, STATIC);
            expectPeek(INT, CHAR, BOOLEAN, IDENTIFIER);
            expectPeek(IDENTIFIER);
            switch (getPeekToken().type) {
                case COMMA -> {
                    while (getPeekToken().type == COMMA) {
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
