package br.ufma.ecp.parser;

import br.ufma.ecp.Command;
import br.ufma.ecp.Parser;
import br.ufma.ecp.Segment;
import br.ufma.ecp.VMWriter;
import br.ufma.ecp.token.TokenType;

import static br.ufma.ecp.token.TokenType.*;

public class SubroutineDec extends SyntaxyParser{


    private static final VMWriter vmWrite = new VMWriter();

    public SubroutineDec(Parser parser, StringBuilder xmlOutput, VMWriter vmWriter) {
        super(parser, xmlOutput, vmWrite);
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

    private void parseExpression(){
        printNonTerminal("expression");
        parseTerm();
        while(isOperator(getPeekToken().type)){
            var op = getPeekToken().type;
            expectPeek(getPeekToken().type);
            parseTerm();
            compileOperators(op);
        }
        printNonTerminal("/expression");
    }

    private void compileOperators(TokenType type){
        if(type == ASTERISK){
            vmWrite.writeCall("Math.multiply",2);
        }else if(type == SLASH){
            vmWrite.writeCall("Math.divide", 2);
        }else{
            vmWrite.writeArithmetic(Command.typeOperator(type));
        }
    }

    private void parseTerm(){
        printNonTerminal("term");
        switch (getPeekToken().type){
            case INT -> {
                expectPeek(INT);
                vmWrite.writePush(Segment.CONST, Integer.parseInt(getCurrentToken().lexeme));
            }
            case STRING -> {
                expectPeek(STRING);
                var strValue = getCurrentToken().lexeme;
                vmWrite.writePush(Segment.CONST, strValue.length());
                vmWrite.writeCall("String.new",1);
                for(int i = 0; i<strValue.length(); i++){
                    vmWrite.writePush(Segment.CONST, strValue.length());
                    vmWrite.writeCall("String.appendChar",2);
                }
            }
            case FALSE, NULL, TRUE -> {
                expectPeek(FALSE, NULL, TRUE);
                vmWrite.writePush(Segment.CONST, 0);
                if(getCurrentToken().type == TRUE){
                    vmWrite.writeArithmetic(Command.NOT);
                }
            }
            case THIS ->{
                expectPeek(THIS);
                vmWrite.writePush(Segment.POINTER, 0);
            }
            case IDENTIFIER -> {
                expectPeek(IDENTIFIER);
                getSymbol(getCurrentToken().lexeme.charAt(0));
                if(getPeekToken().type == LPAREN){
                    parseSubroutineCall();
                }else{
                    if(getPeekToken().type==LBRACKET){
                        expectPeek(LBRACKET);
                        parseExpression();
                        expectPeek(RBRACKET);
                    }
                }
            }
            case LPAREN -> {
                expectPeek(LPAREN);
                parseExpression();
                expectPeek(RPAREN);
            }
            case MINUS, NOT ->{
                expectPeek(MINUS, NOT);
                var op = getCurrentToken().type;
                parseTerm();
            }
        }
    }

    private void parseSubroutineCall() {

        var nArgs = 0;

        var ident = getCurrentToken().lexeme;
        var functionName = ident + ".";

        if (getCurrentToken().type == LPAREN) { // método da propria classe
            expectPeek(LPAREN);
//                vmWriter.writePush(Segment.POINTER, 0);
//                nArgs = parseExpressionList() + 1;
            expectPeek(RPAREN);
//                functionName = className + "." + ident;
        } else {
            // pode ser um metodo de um outro objeto ou uma função
            expectPeek(DOT);
            expectPeek(IDENTIFIER); // nome da função

//                if (symbol != null) { // é um metodo
//                    nArgs = 1; // do proprio objeto
//                } else {
//                    functionName += getCurrentToken().lexeme; // é uma função
//                }

            expectPeek(LPAREN);
//                nArgs += parseExpressionList(); TODO

            expectPeek(RPAREN);
        }

//            vmWriter.writeCall(functionName, nArgs);
    }
}
