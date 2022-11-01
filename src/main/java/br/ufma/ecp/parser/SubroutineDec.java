package br.ufma.ecp.parser;

import br.ufma.ecp.Parser;
import br.ufma.ecp.token.TokenType;

import static br.ufma.ecp.token.TokenType.*;

public class SubroutineDec extends SyntaxyParser{

    public SubroutineDec(Parser parser, StringBuilder xmlOutput) {
        super(parser, xmlOutput);
    }

    public void parseSubroutineDec(){

        while (getPeekToken().type.equals(METHOD) || getPeekToken().type.equals(CONSTRUCTOR)) {
            varMethod();
        }
    }

    private void varMethod() {
        TokenType tokeMethod = getTokenType();
        printNonTerminal("subroutineDec");

        if(tokeMethod.equals(METHOD)) {
            expectPeek(METHOD);
            expectPeek(INT, CHAR, BOOLEAN, VOID);
            expectPeek(IDENTIFIER);
        } else {
            expectPeek(CONSTRUCTOR);
            expectPeek(IDENTIFIER);
            expectPeek(NEW);
        }

        expectPeek(LPAREN);

        parameterList(); // TODO -> FALTA EXPRESSÃO

        expectPeek(RPAREN);
        expectPeek(LBRACKET);

        // MELHORAR
        while (getPeekToken().type.equals(LET) || getPeekToken().type.equals(DO)) {
            switch (getPeekToken().type) {
                case LET -> letStatement();
                case DO -> doStatement();
            }
        }

        expectPeek(RETURN);

        if(tokeMethod.equals(CONSTRUCTOR)){
            expectPeek(THIS);
        }

        expectPeek(SEMICOLON);
        expectPeek(RBRACKET);
        printNonTerminal("/subroutineDec");
    }

    private void doStatement() {
        printNonTerminal("doStatement");
        expectPeek(DO);
        expectPeek(IDENTIFIER);
        switch (getPeekToken().type) {

            case LPAREN -> {
                expectPeek(LPAREN);
                expressionList();
                expectPeek(RPAREN);
                expectPeek(SEMICOLON);
            }
            case DOT -> {
                while (getPeekToken().type.equals(DOT)) {
                    expectPeek(DOT);
                    expectPeek(IDENTIFIER);
                }
                expectPeek(LPAREN);
                expressionList(); // TODO-> PODE SER UMA EXPRESSÃO // PODE SER VAZIA
                expectPeek(RPAREN);
                expectPeek(SEMICOLON);
            }
        }
        printNonTerminal("/doStatement");
    }

    private void letStatement() {
        printNonTerminal("letStatement");
        expectPeek(LET);
        expectPeek(IDENTIFIER);
        expectPeek(EQ);
        // TODO - > PODE SER UMA EXPRESSÃO
        expectPeek(NUMBER, STRING, BOOLEAN, IDENTIFIER);
        expectPeek(SEMICOLON);
        printNonTerminal("/letStatement");

        if(getPeekToken().type.equals(LET)) {
            letStatement();
        }
    }

    private void expressionList() {
        // if (((y + size) < 254) & ((x + size) < 510)) {}
    }

    private void parameterList() {
        switch (getPeekToken().type) {
            case INT, CHAR, BOOLEAN, STRING -> {
                printNonTerminal("parameterList");
                expectPeek(INT, STRING, BOOLEAN, CHAR);
                expectPeek(IDENTIFIER);
                while (getPeekToken().type == COMMA) {
                    expectPeek(COMMA);
                    expectPeek(INT, STRING, BOOLEAN, CHAR);
                    expectPeek(IDENTIFIER);
                }
                printNonTerminal("/parameterList");
            }
        }
    }

}
