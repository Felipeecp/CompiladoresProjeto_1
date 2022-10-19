package br.ufma.ecp;

import br.ufma.ecp.token.Token;
import br.ufma.ecp.token.TokenType;

import static br.ufma.ecp.token.TokenType.*;
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
        match(NUMBER);
    }

    private void nextToken () {
        currentToken = scan.nextToken();
    }

    private void match(TokenType t) {
        if (currentToken.type == t) {
            nextToken();
        }else {
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

    private void expectPeek (TokenType... types) {
        for (TokenType type : types) {
            if (peekToken.type == type ) {
                expectPeek(type);
                return;
            }
        }

        throw new Error("Syntax error");

    }

    // term -> number | identifier | stringConstant | keywordConstant
    void parserTerm () {
        System.out.println("<term>");
        switch (peekToken.type) {
            case NUMBER:
                expectPeek(NUMBER);
                break;
            case IDENTIFIER:
                expectPeek(IDENTIFIER);
                break;
            case STRING:
                expectPeek(STRING);
                break;
            case FALSE:
            case NULL:
            case TRUE:
                expectPeek(FALSE,NULL,TRUE);
                break;
            default:
                ;
        }
        System.out.println("</term>");
    }

    private boolean isOperator (TokenType type) {
        return type.ordinal() >= PLUS.ordinal() && type.ordinal() <= EQ.ordinal();
    }

    // 'while' '(' expression ')' '{' statements '}'
    void parseWhile () {
        System.out.println("<whileStatement>");
        expectPeek(WHILE);
        expectPeek(LPAREN);
        parseExpression();
        expectPeek(RPAREN);
        expectPeek(LBRACE);
        parseStatements();
        expectPeek(RBRACE);
        System.out.println("</whileStatement>");
    }

    void parseStatements () {
        System.out.println("<statements>");
        while (peekToken.type == WHILE ||
                peekToken.type == IF ||
                peekToken.type == LET ||
                peekToken.type == DO ||
                peekToken.type == RETURN ) {
            parseStatement();
        }

        System.out.println("</statements>");
    }

    void parseStatement () {
        switch (peekToken.type) {
            case LET:
                parseLet();
                break;
            case WHILE:
                parseWhile();
                break;
            default:
                throw new Error("Syntax error - expected a statement");
        }
    }

    void parseIf () {
        System.out.println("<ifStatement>");
        expectPeek(IF);
        expectPeek(LPAREN);
        parseExpression();
        expectPeek(RPAREN);
        expectPeek(LBRACE);
        parseStatements();
        expectPeek(RBRACE);
        System.out.println("</ifStatement>");
    }

    void parseExpressionList() {
        printNonTerminal("expressionList");

        if (!peekTokenIs(RPAREN)) // verifica se tem pelo menos uma expressao
        {
            parseExpression();
        }

        // procurando as demais
        while (peekTokenIs(COMMA)) {
            expectPeek(COMMA);
            parseExpression();
        }

        printNonTerminal("/expressionList");
    }

    void parseSubroutineCall () {
        if (peekTokenIs (LPAREN)) {
            expectPeek(LPAREN);
            parseExpressionList();
            expectPeek(RPAREN);
        } else {
            // pode ser um metodo de um outro objeto ou uma função
            expectPeek(DOT);
            expectPeek(IDENTIFIER);
            expectPeek(LPAREN);
            parseExpressionList();
            expectPeek(RPAREN);
        }
    }

    void parseClassVarDec () {
        printNonTerminal("classVarDec");
        expectPeek(FIELD,STATIC);
        // 'int' | 'char' | 'boolean' | className
        expectPeek(INT,CHAR,BOOLEAN,IDENTIFIER);
        expectPeek(IDENTIFIER);
        expectPeek(SEMICOLON);
        printNonTerminal("/classVarDec");
    }

    void parseClassVarDec () {
        printNonTerminal("classVarDec");
        expectPeek(FIELD,STATIC);
        // 'int' | 'char' | 'boolean' | className
        expectPeek(INT,CHAR,BOOLEAN,IDENTIFIER);
        expectPeek(IDENTIFIER);

        while (peekTokenIs(COMMA)) {
            expectPeek(COMMA);
            expectPeek(IDENTIFIER);
        }

        expectPeek(SEMICOLON);
        printNonTerminal("/classVarDec");
    }

    void parseSubroutineDec () {
        printNonTerminal("classVarDec");
        expectPeek(CONSTRUCTOR, FUNCTION, METHOD);
        // 'int' | 'char' | 'boolean' | className
        expectPeek(VOID, INT,CHAR,BOOLEAN,IDENTIFIER);
        expectPeek(IDENTIFIER);

        expectPeek(LPAREN);
        parseParameterList();
        expectPeek(RPAREN);
        parseSubroutineBody();

        printNonTerminal("/classVarDec");
    }


    void parseParameterList()
    {
        printNonTerminal("parameterList");


        if (!peekTokenIs(RPAREN)) // verifica se tem pelo menos uma expressao
        {
            expectPeek(INT,CHAR,BOOLEAN,IDENTIFIER);
            expectPeek(IDENTIFIER);
        }


        while (peekTokenIs(COMMA))
        {
            expectPeek(COMMA);
            expectPeek(INT,CHAR,BOOLEAN,IDENTIFIER);
            expectPeek(IDENTIFIER);
        }

        printNonTerminal("/parameterList");
    }

    void parseSubroutineBody () {
        expectPeek(LBRACE);
        parseStatements();
        expectPeek(RBRACE);
    }

    void parseSubroutineDec () {
        printNonTerminal("classVarDec");
        expectPeek(CONSTRUCTOR, FUNCTION, METHOD);
        // 'int' | 'char' | 'boolean' | className
        expectPeek(VOID, INT,CHAR,BOOLEAN,IDENTIFIER);
        expectPeek(IDENTIFIER);

        expectPeek(LPAREN);
        parseParameterList();
        expectPeek(RPAREN);
        parseSubroutineBody();

        printNonTerminal("/classVarDec");
    }


    void parseParameterList()
    {
        printNonTerminal("parameterList");


        if (!peekTokenIs(RPAREN)) // verifica se tem pelo menos uma expressao
        {
            expectPeek(INT,CHAR,BOOLEAN,IDENTIFIER);
            expectPeek(IDENTIFIER);
        }


        while (peekTokenIs(COMMA))
        {
            expectPeek(COMMA);
            expectPeek(INT,CHAR,BOOLEAN,IDENTIFIER);
            expectPeek(IDENTIFIER);
        }

        printNonTerminal("/parameterList");
    }

    void parseVarDec () {
        printNonTerminal("varDec");
        expectPeek(VAR);
        // 'int' | 'char' | 'boolean' | className
        expectPeek(INT,CHAR,BOOLEAN,IDENTIFIER);
        expectPeek(IDENTIFIER);

        while (peekTokenIs(COMMA)) {
            expectPeek(COMMA);
            expectPeek(IDENTIFIER);
        }

        expectPeek(SEMICOLON);
        printNonTerminal("/varDec");
    }


    public String VMOutput() {
        return "";
    }

}
