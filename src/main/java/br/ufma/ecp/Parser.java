package br.ufma.ecp;

import br.ufma.ecp.token.Token;
import br.ufma.ecp.token.TokenType;

public class Parser {
    private Scanner scan;
    private Token currentToken;
    
    public Parser (byte[] input) {
        scan = new Scanner(input);
        currentToken = scan.nextToken();
        
    }

    public void parse () {
        statements();
    }

    void letStatement () {
        match(TokenType.LET);
        var id = currentToken.lexeme;
        match(TokenType.IDENT);
        match(TokenType.EQ);
        expr();
        System.out.println("pop "+id);
        match(TokenType.SEMICOLON);
    }

    void printStatement () {
        match(TokenType.PRINT);
        expr();
        System.out.println("print");
        match(TokenType.SEMICOLON);
    }

    void statements () {

        while (currentToken.type != TokenType.EOF) {
            System.out.println("Current token " + currentToken.type);
            statement();
        }
    }

    void statement () {
        if (currentToken.type == TokenType.PRINT) {
            printStatement();
        } else if (currentToken.type == TokenType.LET) {
            letStatement();
        } else {
            throw new Error("syntax error");
        }
    }

    void expr() {
        number();
        oper();
    }

    void number () {
        System.out.println(currentToken.lexeme);
        match(TokenType.NUMBER);
    }

    private void nextToken () {
        currentToken = scan.nextToken();
    }

   private void match(TokenType t) {

        if (currentToken.type == t) {
            nextToken();
        } else if (TokenType.isSymbol(currentToken.lexeme.charAt(0))) {

        }
        else {
            throw new Error("syntax error");
        }
   }

    void oper () {
        if (currentToken.type == TokenType.PLUS) {
            match(TokenType.PLUS);
            number();
            System.out.println("add");
            oper();
        } else if (currentToken.type == TokenType.MINUS) {
            match(TokenType.MINUS);
            number();
            System.out.println("sub");
            oper();
        }
    }

    public String VMOutput() {
        return "";
    }

}
