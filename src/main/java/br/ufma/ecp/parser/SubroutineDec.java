package br.ufma.ecp.parser;

import br.ufma.ecp.Parser;
import br.ufma.ecp.token.TokenType;

import static br.ufma.ecp.token.TokenType.*;

public class SubroutineDec extends SyntaxyParser {


    public SubroutineDec(Parser parser, StringBuilder xmlOutput) {
        super(parser, xmlOutput);
    }

    public void parseSubroutineDec() {

        while (getPeekTokenType().equals(METHOD) || getPeekTokenType().equals(CONSTRUCTOR)) {
            varMethod();
        }
    }

    public void varMethod() {
        TokenType tokeMethod = getPeekTokenType();
        printNonTerminal("subroutineDec");

        if (tokeMethod.equals(METHOD)) {
            expectPeek(METHOD);
            expectPeek(INT, CHAR, BOOLEAN, VOID);
            expectPeek(IDENTIFIER);
        } else {
            expectPeek(CONSTRUCTOR);
            expectPeek(IDENTIFIER);
            expectPeek(IDENTIFIER);
        }

        expectPeek(LPAREN);

        parameterList();

        expectPeek(RPAREN);

        parseSubroutineBody();

        printNonTerminal("/subroutineDec");
    }

    private void parseSubroutineBody(){
        printNonTerminal("subroutineBody");
        expectPeek(LBRACKET);
        while (getPeekTokenType() == VAR){
            parseVarDec();
        }

        parseStatements();
        expectPeek(RBRACKET);
        printNonTerminal("/subroutineBody");
    }

    void parseStatements () {
        printNonTerminal("statements");
        while (getPeekTokenType() == WHILE ||
                getPeekTokenType() == IF ||
                getPeekTokenType() == LET ||
                getPeekTokenType() == DO ||
                getPeekTokenType()== RETURN ) {
            parseStatement();
        }

        printNonTerminal("/statements");
    }

    void parseStatement () {
        switch (getPeekTokenType()) {
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

        while (getPeekTokenType() == COMMA) {
            expectPeek(COMMA);
            expectPeek(IDENTIFIER);
        }

        expectPeek(SEMICOLON);
        printNonTerminal("/varDec");
    }

    public void parseIf() {
        printNonTerminal("ifStatement");

        expectPeek(IF);
        expectPeek(LPAREN);
        parseExpression();
        expectPeek(RPAREN);

        expectPeek(LBRACKET);
        parseStatements();
        expectPeek(RBRACKET);

        if (getPeekTokenType() == ELSE)
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
        if (!(getPeekTokenType() == SEMICOLON)) {
            parseExpression();
        }
        expectPeek(SEMICOLON);

        printNonTerminal("/returnStatement");
    }


    public void parseDo() {
        printNonTerminal("doStatement");
        expectPeek(DO);
        expectPeek(IDENTIFIER);
        parseSubroutineCall();
        expectPeek(SEMICOLON);
        printNonTerminal("/doStatement");
    }

    public void parseLet() {
        printNonTerminal("letStatement");
        expectPeek(LET);
        expectPeek(IDENTIFIER);

        if(getPeekTokenType() == LBRACKET){
            expectPeek(LBRACKET);
            parseExpression();
            expectPeek(RBRACKET);
        }
        expectPeek(EQ);
        parseExpression();
        expectPeek(SEMICOLON);
        printNonTerminal("/letStatement");

        if(getPeekTokenType().equals(LET)) {
            parseLet();
        }
    }

    private int parseExpressionList() {
        printNonTerminal("expressionList");

        var nArgs = 0;

        if(!(getPeekTokenType() == RPAREN)){
           parseExpression();
           nArgs = 1;
        }

        while (getPeekTokenType() == COMMA){
            expectPeek(COMMA);
            parseExpression();
            nArgs++;
        }


        printNonTerminal("/expressionList");
        return nArgs;
    }

    private void parameterList() {
        printNonTerminal("parameterList");
        switch (getPeekTokenType()) {
            case INT, CHAR, BOOLEAN, STRING -> {
                expectPeek(INT, STRING, BOOLEAN, CHAR);
                expectPeek(IDENTIFIER);
                while (getPeekTokenType() == COMMA) {
                    expectPeek(COMMA);
                    expectPeek(INT, STRING, BOOLEAN, CHAR);
                    expectPeek(IDENTIFIER);
                }
            }
        }
        printNonTerminal("/parameterList");
    }

    private void parseExpression(){
        printNonTerminal("expression");
        parseTerm();
        while(isOperator(getPeekTokenType())){
            expectPeek(getPeekTokenType());
            parseTerm();
        }
        printNonTerminal("/expression");
    }

    private void parseTerm(){
        printNonTerminal("term");
        switch (getPeekTokenType()){
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
                if(getPeekTokenType() == LPAREN || getPeekTokenType() == DOT){
                    parseSubroutineCall();
                }else{
                    if(getPeekTokenType()==LBRACKET){
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
        printNonTerminal("/term");
    }

    private void parseSubroutineCall(){

        if (getPeekTokenType() == LPAREN) {
            expectPeek(LPAREN);
            parseExpressionList();
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
