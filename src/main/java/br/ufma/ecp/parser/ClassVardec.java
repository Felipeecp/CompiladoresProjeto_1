package br.ufma.ecp.parser;

import br.ufma.ecp.Kind;
import br.ufma.ecp.Parser;
import br.ufma.ecp.SymbolTable;
import br.ufma.ecp.VMWriter;

import static br.ufma.ecp.token.TokenType.*;
import static br.ufma.ecp.token.TokenType.SEMICOLON;

public class ClassVardec extends SyntaxyParser{

    public ClassVardec(Parser parser, SymbolTable symbolTable, VMWriter vmWriter, StringBuilder xmlOutput) {
        super(parser, symbolTable,vmWriter,xmlOutput);
    }

    public void parseClassVarDec () {
        while (getPeekTokenType() == FIELD || getPeekTokenType() == STATIC) {
            printNonTerminal("classVarDec");
            expectPeek(FIELD, STATIC);

            Kind kind = Kind.STATIC;
            if(getCurrentTokenType() == FIELD){
                kind = Kind.FIELD;
            }

            expectPeek(INT, CHAR, BOOLEAN, IDENTIFIER);
            String type = getCurrentTokenType().lexeme;

            expectPeek(IDENTIFIER);
            String name = getCurrentTokenType().lexeme;

            getSymbolTable().define(name, type, kind);

            switch (getPeekTokenType()) {
                case COMMA -> {
                    while (getPeekTokenType() == COMMA) {
                        expectPeek(COMMA);
                        expectPeek(IDENTIFIER);

                        name = getCurrentTokenType().lexeme;
                        getSymbolTable().define(name, type, kind);
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
