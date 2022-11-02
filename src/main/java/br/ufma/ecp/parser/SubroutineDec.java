package br.ufma.ecp.parser;

import br.ufma.ecp.Parser;
import br.ufma.ecp.token.TokenType;

import static br.ufma.ecp.token.TokenType.*;

public class SubroutineDec extends SyntaxyParser {


    public SubroutineDec(Parser parser, StringBuilder xmlOutput) {
        super(parser, xmlOutput);
    }

    public void parseSubroutineDec() {

        while (getPeekToken().type.equals(METHOD) || getPeekToken().type.equals(CONSTRUCTOR)) {
            varMethod();
        }
    }

    private void varMethod() {
        TokenType tokeMethod = getTokenType();
        printNonTerminal("subroutineDec");

        if (tokeMethod.equals(METHOD)) {
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

        parseSubroutineBody();

        printNonTerminal("/subroutineDec");
    }

    private void parseSubroutineBody(){
        printNonTerminal("subroutineBody");
        expectPeek(LBRACKET);
        while (getPeekToken().type == LBRACKET){
            parseVarDec();
        }

        parseStatements();
        expectPeek(RBRACKET);
        printNonTerminal("/subroutineBody");
    }

    void parseStatements () {
        System.out.println("<statements>");
        while (getTokenType() == WHILE ||
                getTokenType() == IF ||
                getTokenType() == LET ||
                getTokenType() == DO ||
                getTokenType()== RETURN ) {
            parseStatement();
        }

        System.out.println("</statements>");
    }

    void parseStatement () {
        switch (getTokenType()) {
            case LET -> parseLet();
            case WHILE -> parseWhile();
            case IF -> parseIf();
            case RETURN -> parseReturn();
            case DO -> parseDo();
            default -> {
                throw new Error("Syntax error - expected a statement");
            }
        }
    }

    void parseWhile () {
        printNonTerminal("whileStatement");
        expectPeek(WHILE);
        expectPeek(LPAREN);
        parseExpression();
        expectPeek(RPAREN);
        expectPeek(LBRACKET);
        parseStatements();
        expectPeek(RBRACKET);
        printNonTerminal("/whileStatement");
    }

    void parseVarDec () {
        printNonTerminal("varDec");
        expectPeek(VAR);
        expectPeek(INT,CHAR,BOOLEAN,IDENTIFIER);
        expectPeek(IDENTIFIER);

        while (getPeekToken().type == COMMA) {
            expectPeek(COMMA);
            expectPeek(IDENTIFIER);
        }

        expectPeek(SEMICOLON);
        printNonTerminal("/varDec");
    }

    void parseIf() {
        printNonTerminal("ifStatement");

        expectPeek(IF);
        expectPeek(LPAREN);
        parseExpression();
        expectPeek(RPAREN);

        expectPeek(LBRACKET);
        parseStatements();
        expectPeek(RBRACKET);

        if (getTokenType() == ELSE)
        {
            expectPeek(ELSE);

            expectPeek(LBRACKET);

            parseStatements();

            expectPeek(RBRACKET);
        }

        printNonTerminal("/ifStatement");
    }

    void parseReturn() {
        printNonTerminal("returnStatement");
        expectPeek(RETURN);
        if (!(getTokenType() == SEMICOLON)) {
            parseExpression();
        }
        expectPeek(SEMICOLON);

        printNonTerminal("/returnStatement");
    }


    private void parseDo() {
        printNonTerminal("doStatement");
        expectPeek(DO);
        expectPeek(IDENTIFIER);
        parseSubroutineCall();
        expectPeek(SEMICOLON);
        printNonTerminal("/doStatement");
    }

    private void parseLet() {
        printNonTerminal("letStatement");
        expectPeek(LET);
        expectPeek(IDENTIFIER);
        if(getTokenType() == LBRACKET){
            expectPeek(LBRACKET);
            parseExpression();
            expectPeek(RBRACKET);
        }
        expectPeek(EQ);
        parseExpression();
        expectPeek(SEMICOLON);
        printNonTerminal("/letStatement");

        if(getPeekToken().type.equals(LET)) {
            parseLet();
        }
    }

    private int parseExpressionList() {
        printNonTerminal("expressionList");

        var nArgs = 0;

        if(!(getPeekToken().type == RPAREN)){
           parseExpression();
           nArgs = 1;
        }

        while (getPeekToken().type == COMMA){
            expectPeek(COMMA);
            parseExpression();
            nArgs++;
        }


        printNonTerminal("/expressionList");
        return nArgs;
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
            expectPeek(getPeekToken().type);
            parseTerm();
        }
        printNonTerminal("/expression");
    }

    private void parseTerm(){
        printNonTerminal("term");
        switch (getPeekToken().type){
            case NUMBER -> {
                expectPeek(NUMBER);
            }
            case STRING -> {
                expectPeek(STRING);
            }
            case FALSE, NULL, TRUE -> {
                expectPeek(FALSE, NULL, TRUE);
            }
            case THIS ->{
                expectPeek(THIS);

            }
            case IDENTIFIER -> {
                expectPeek(IDENTIFIER);
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
                parseTerm();
            }
        }
    }

    private void parseSubroutineCall(){

        if (getTokenType() == LPAREN) {
            expectPeek(LPAREN);
            expectPeek(RPAREN);
        } else {
            expectPeek(DOT);
            expectPeek(IDENTIFIER);

            expectPeek(LPAREN);
            parseExpressionList();
            expectPeek(RPAREN);
        }

    }
}
