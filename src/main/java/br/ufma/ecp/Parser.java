package br.ufma.ecp;

import static br.ufma.ecp.token.TokenType.*;

import br.ufma.ecp.token.Token;
import br.ufma.ecp.token.TokenType;

public class Parser {

    private Scanner scan;
    private Token currentToken;
    private Token peekToken;

    private StringBuilder xmlOutput = new StringBuilder();

    public Parser (byte[] input) {
        scan = new Scanner(input);
        nextToken();
    }

    private void nextToken() {
        currentToken = peekToken;
        peekToken = scan.nextToken();
    }

   

    void parser () {
        parseClass();
    }
    //'class' className '{' classVarDec* subroutineDec* '}'
    //'class' className '{' classVarDec '}'
    void parseClass() {
        printNonTerminal("class");
        expectPeek(CLASS);
        expectPeek(IDENTIFIER);
        expectPeek(LBRACE);

        // A classe pode ter fields; static and funtions/construct/methods
        classVardec();
        subroutineDec();

        
        expectPeek(RBRACE);
        printNonTerminal("/class");
    }
    void parseSubroutineDec(){
        printNonTerminal("subroutineDec");
            expectPeek(FUNCTION);
            expectPeek(INT, CHAR, BOOLEAN, IDENTIFIER, VOID);
            expectPeek(IDENTIFIER);
            expectPeek(LPAREN);
            expectPeek(RPAREN);
            expectPeek(LBRACE);
            classVardec();
            expectPeek(RBRACE);
            expectPeek(SEMICOLON);
        printNonTerminal("/subroutineDec");
    }

    //( 'static' | 'field' ) type varName ( ',' varName)* ';'
    void parseClassVardec() {
        printNonTerminal("classVarDec");
        expectPeek(FIELD, STATIC);
        expectPeek(INT, CHAR, BOOLEAN, IDENTIFIER);
        expectPeek(IDENTIFIER);
        switch (peekToken.type) {
            case COMMA -> {
                while (peekToken.type == COMMA) {
                    expectPeek(COMMA);
                    expectPeek(IDENTIFIER);
                }
            }
            case EQ -> {
                // pode inicializar a variável
                // exemplo: static int x = 10;
                // exemplo: field int a = b;
                expectPeek(EQ);
                expectPeek(NUMBER, STRING, BOOLEAN);
            }
        }
        expectPeek(SEMICOLON);
        printNonTerminal("/classVarDec");
    }

    void subroutineDec(){
        if(peekToken.type == FUNCTION) {
            parseSubroutineDec();
        }
    }

    void classVardec(){
        while (peekToken.type == FIELD || peekToken.type == STATIC) {
            parseClassVardec();
        }
    }


    // letStatement -> 'let' varName  '=' term ';'
    // term -> number;
    void parseLet() {
        printNonTerminal("letStatement");
        expectPeek(LET);
        expectPeek(IDENTIFIER);
        expectPeek(EQ);
        parseTerm();
        expectPeek(SEMICOLON);
        printNonTerminal("/letStatement");

    }

    void parseExpression () {
 
    }

    void parseTerm () {
        printNonTerminal("term");
        switch (peekToken.type) {
            case NUMBER:
                expectPeek(NUMBER);
                break;
            default:
                ;

        }
        printNonTerminal("/term");
    }
    // auxiliares

    boolean currentTokenIs (TokenType type) {
        return currentToken.type == type;
    }


    boolean peekTokenIs (TokenType type) {
        return peekToken.type == type;
    }


    private void expectPeek (TokenType type) {
        if (peekToken.type == type ) {
            nextToken();
            xmlOutput.append(String.format("%s\r\n", currentToken.toString()));
        } else {
            throw new Error("Syntax error - expected "+type+" found " + peekToken.lexeme);
        }
    }

    private void expectPeek(TokenType... types) {
        
        for (TokenType type : types) {
            if (peekToken.type == type) {
                expectPeek(type);
                return;
            }
        }
        
        throw new Error("Syntax error ");

    }


    public String XMLOutput() {
        return xmlOutput.toString();
    }

    private void printNonTerminal(String nterminal) {
        xmlOutput.append(String.format("<%s>\r\n", nterminal));
    }



  

}
