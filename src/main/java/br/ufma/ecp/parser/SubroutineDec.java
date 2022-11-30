package br.ufma.ecp.parser;

import br.ufma.ecp.*;
import br.ufma.ecp.token.TokenType;

import static br.ufma.ecp.token.TokenType.*;

public class SubroutineDec extends SyntaxyParser {


    private int ifLabelNum;
    private int whileLabelNum;

    public SubroutineDec(Parser parser, SymbolTable symbolTable, VMWriter vmWriter, StringBuilder xmlOutput) {
        super(parser, symbolTable, vmWriter,xmlOutput);

        ifLabelNum = 0;
        whileLabelNum = 0;

    }

    public void parseSubroutineDec() {

        while (getPeekTokenType().equals(METHOD) || getPeekTokenType().equals(CONSTRUCTOR)) {
            varMethod();
        }
    }

    public void varMethod() {
        TokenType tokeMethod = getPeekTokenType();
        printNonTerminal("subroutineDec");

        ifLabelNum = 0;
        whileLabelNum = 0;

        getSymbolTable().startSubroutine();

        var subroutineType = getPeekTokenType();

        if (tokeMethod.equals(METHOD) || tokeMethod.equals(FUNCTION)) {
            if(tokeMethod.equals(METHOD)) {
                getSymbolTable().define("this", getClassName(),  Kind.ARG);
                expectPeek(METHOD);
            } else {
                expectPeek(FUNCTION);
            }

            expectPeek(INT, CHAR, BOOLEAN, VOID);

            if(getPeekTokenType() == SEMICOLON) {
                expectPeek(SEMICOLON);
                expectPeek(INT, CHAR, BOOLEAN, VOID);
            }

            expectPeek(IDENTIFIER);
        } else {
            expectPeek(CONSTRUCTOR);
            expectPeek(IDENTIFIER);
            expectPeek(IDENTIFIER);
        }

        var functionName = getClassName() + "." + getCurrentTokeLexeme();

        expectPeek(LPAREN);

        parameterList();

        expectPeek(RPAREN);

        parseSubroutineBody(functionName,subroutineType);

        printNonTerminal("/subroutineDec");
    }

    private void parseSubroutineBody(String functionName,TokenType subroutineType){
        printNonTerminal("subroutineBody");
        expectPeek(LBRACKET);
        while (getPeekTokenType() == VAR){
            parseVarDec();
        }
        var nlocals = getSymbolTable().varCount(Kind.VAR);

        getVmWriter().writeFunction(functionName, nlocals);

        if (subroutineType == CONSTRUCTOR) {
            getVmWriter().writePush(Segment.CONST, getSymbolTable().varCount(Kind.FIELD));
            getVmWriter().writeCall("Memory.alloc", 1);
            getVmWriter().writePop(Segment.POINTER, 0);
        }

        if (subroutineType == METHOD) {
            getVmWriter().writePush(Segment.ARG, 0);
            getVmWriter().writePop(Segment.POINTER, 0);
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

        var labelTrue = "WHILE_EXP" + whileLabelNum;
        var labelFalse = "WHILE_END" + whileLabelNum;
        whileLabelNum++;

        getVmWriter().writeLabel(labelTrue);

        expectPeek(WHILE);
        expectPeek(LPAREN);
        parseExpression();

        getVmWriter().writeArithmetic(Command.NOT);
        getVmWriter().writeIf(labelFalse);

        expectPeek(RPAREN);
        expectPeek(LBRACKET);
        parseStatements();

        getVmWriter().writeGoto(labelTrue);
        getVmWriter().writeLabel(labelFalse);

        expectPeek(RBRACKET);
        printNonTerminal("/whileStatement");

    }

    void parseVarDec () {
        printNonTerminal("varDec");
        expectPeek(VAR);

        Kind kind = Kind.VAR;

        expectPeek(INT,CHAR,BOOLEAN,IDENTIFIER);
        String type = getCurrentTokeLexeme();

        expectPeek(IDENTIFIER);
        String name = getCurrentTokeLexeme();
        getSymbolTable().define(name, type, kind);

        while (getPeekTokenType() == COMMA) {
            expectPeek(COMMA);
            expectPeek(IDENTIFIER);

            name = getCurrentTokeLexeme();
            getSymbolTable().define(name, type, kind);
        }

        expectPeek(SEMICOLON);
        printNonTerminal("/varDec");
    }

    public void parseIf() {
        printNonTerminal("ifStatement");

        var labelTrue = "IF_TRUE" + ifLabelNum;
        var labelFalse = "IF_FALSE" + ifLabelNum;
        var labelEnd = "IF_END" + ifLabelNum;

        ifLabelNum++;

        expectPeek(IF);
        expectPeek(LPAREN);
        parseExpression();
        expectPeek(RPAREN);

        getVmWriter().writeIf(labelTrue);
        getVmWriter().writeGoto(labelFalse);
        getVmWriter().writeLabel(labelTrue);

        expectPeek(LBRACKET);
        parseStatements();
        expectPeek(RBRACKET);


        if (getPeekTokenType() == ELSE)
        {
            getVmWriter().writeGoto(labelEnd);
        }

        getVmWriter().writeLabel(labelFalse);

        if (getPeekTokenType() == ELSE)
        {
            expectPeek(ELSE);

            expectPeek(LBRACKET);

            parseStatements();

            expectPeek(RBRACKET);
            getVmWriter().writeLabel(labelEnd);
        }

        printNonTerminal("/ifStatement");

    }

    void parseReturn() {
        printNonTerminal("returnStatement");
        expectPeek(RETURN);
        if (!(getPeekTokenType() == SEMICOLON)) {
            parseExpression();
        }else {
            getVmWriter().writePush(Segment.CONST, 0);
        }
        expectPeek(SEMICOLON);
        getVmWriter().writeReturn();


        printNonTerminal("/returnStatement");
    }


    public void parseDo() {
        printNonTerminal("doStatement");
        expectPeek(DO);
        expectPeek(IDENTIFIER);
        parseSubroutineCall();
        expectPeek(SEMICOLON);
        getVmWriter().writePop(Segment.TEMP, 0);

        printNonTerminal("/doStatement");
    }

    public void parseLet() {
        printNonTerminal("letStatement");

        var isArray = false;

        expectPeek(LET);
        expectPeek(IDENTIFIER);

        var symbol = getSymbolTable().resolve(getCurrentTokeLexeme());

        if(getPeekTokenType() == LBRACE){
            expectPeek(LBRACE);
            parseExpression();

            getVmWriter().writePush(kind2Segment(symbol.kind()), symbol.index());
            getVmWriter().writeArithmetic(Command.ADD);

            expectPeek(RBRACE);

            isArray = true;
        }

        expectPeek(EQ);
        parseExpression();

        if (isArray) {
            getVmWriter().writePop(Segment.TEMP, 0);
            getVmWriter().writePop(Segment.POINTER, 1);
            getVmWriter().writePush(Segment.TEMP, 0);
            getVmWriter().writePop(Segment.THAT, 0);
        } else {
            getVmWriter().writePop(kind2Segment(symbol.kind()), symbol.index());
        }


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

        Kind kind = Kind.ARG;

        switch (getPeekTokenType()) {
            case INT, CHAR, BOOLEAN, STRING -> {
                expectPeek(INT, STRING, BOOLEAN, CHAR);
                String type = getCurrentTokeLexeme();

                expectPeek(IDENTIFIER);
                String name = getCurrentTokeLexeme();
                getSymbolTable().define(name, type, kind);

                while (getPeekTokenType() == COMMA) {
                    expectPeek(COMMA);
                    expectPeek(INT, STRING, BOOLEAN, CHAR);
                    type = getCurrentTokenType().lexeme;

                    expectPeek(IDENTIFIER);
                    name = getCurrentTokeLexeme();

                    getSymbolTable().define(name, type, kind);
                }
            }
        }
        printNonTerminal("/parameterList");
    }

    private void parseExpression(){
        printNonTerminal("expression");
        parseTerm();
        while(isOperator(getPeekTokenType())){
            var op = getPeekTokenType();
            expectPeek(getPeekTokenType());
            parseTerm();
            compileOperators(op);
        }
        printNonTerminal("/expression");
    }

    private void parseTerm(){
        printNonTerminal("term");
        switch (getPeekTokenType()){
            case NUMBER -> {
                expectPeek(NUMBER);
                getVmWriter().writePush(Segment.CONST, Integer.parseInt(getCurrentTokeLexeme()));
            }
            case STRING -> {
                expectPeek(STRING);
                var strValue = getCurrentTokeLexeme();
                getVmWriter().writePush(Segment.CONST, strValue.length());
                getVmWriter().writeCall("String.new", 1);
                for (int i = 0; i < strValue.length(); i++) {
                    getVmWriter().writePush(Segment.CONST, strValue.charAt(i));
                    getVmWriter().writeCall("String.appendChar", 2);
                }
            }
            case FALSE, NULL, TRUE -> {
                expectPeek(FALSE, NULL, TRUE);
                getVmWriter().writePush(Segment.CONST, 0);
                if (getCurrentTokenType() == TRUE)
                    getVmWriter().writeArithmetic(Command.NOT);
            }
            case THIS ->{
                expectPeek(THIS);
                getVmWriter().writePush(Segment.POINTER, 0);

            }
            case IDENTIFIER -> {
                expectPeek(IDENTIFIER);
                SymbolTable.Symbol sym = getSymbolTable().resolve(getCurrentTokeLexeme());

                if(getPeekTokenType() == LPAREN || getPeekTokenType() == DOT){
                    parseSubroutineCall();
                }else{
                    if(getPeekTokenType()==LBRACE){
                        expectPeek(LBRACE);
                        parseExpression();

                        getVmWriter().writePush(kind2Segment(sym.kind()), sym.index());
                        getVmWriter().writeArithmetic(Command.ADD);

                        expectPeek(RBRACE);
                        getVmWriter().writePop(Segment.POINTER, 1);
                        getVmWriter().writePush(Segment.THAT, 0);

                    } else {

                        getVmWriter().writePush(kind2Segment(sym.kind()), sym.index());
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
                var op = getCurrentTokenType();
                parseTerm();
                if (op == MINUS)
                    getVmWriter().writeArithmetic(Command.NEG);
                else
                    getVmWriter().writeArithmetic(Command.NOT);
            }
        }
        printNonTerminal("/term");
    }

    private void parseSubroutineCall(){

        var nArgs = 0;

        var ident = getCurrentTokeLexeme();
        var symbol = getSymbolTable().resolve(ident); // classe ou objeto
        var functionName = ident + ".";


        if (getPeekTokenType() == LPAREN) {
            expectPeek(LPAREN);
            getVmWriter().writePush(Segment.POINTER, 0);
            nArgs = parseExpressionList() + 1;
            expectPeek(RPAREN);
            functionName = getClassName() + "." + ident;
        } else {

            expectPeek(DOT);
            expectPeek(IDENTIFIER);

            if (symbol != null) {
                functionName = symbol.type() + "." + getCurrentTokeLexeme();
                getVmWriter().writePush(kind2Segment(symbol.kind()), symbol.index());
                nArgs = 1;
            } else {
                functionName += getCurrentTokeLexeme();
            }

            expectPeek(LPAREN);
            nArgs += parseExpressionList();

            expectPeek(RPAREN);
        }
        getVmWriter().writeCall(functionName, nArgs);

    }

    void compileOperators(TokenType type) {

        if (type == ASTERISK) {
            getVmWriter().writeCall("Math.multiply", 2);
        } else if (type == SLASH) {
            getVmWriter().writeCall("Math.divide", 2);
        } else {
            getVmWriter().writeArithmetic(typeOperator(type));
        }
    }

    private Segment kind2Segment(Kind kind) {
        if (kind == Kind.STATIC)
            return Segment.STATIC;
        if (kind == Kind.FIELD)
            return Segment.THIS;
        if (kind == Kind.VAR)
            return Segment.LOCAL;
        if (kind == Kind.ARG)
            return Segment.ARG;
        return null;
    }

    private Command typeOperator(TokenType type) {
        if (type == PLUS)
            return Command.ADD;
        if (type == MINUS)
            return Command.SUB;
        if (type == LT)
            return Command.LT;
        if (type == GT)
            return Command.GT;
        if (type == EQ)
            return Command.EQ;
        if (type == AND)
            return Command.AND;
        if (type == OR)
            return Command.OR;
        return null;
    }





}
