package br.ufma.ecp.parser;

import br.ufma.ecp.Parser;
import br.ufma.ecp.SymbolTable;
import br.ufma.ecp.VMWriter;
import br.ufma.ecp.token.Token;
import br.ufma.ecp.token.TokenType;

public abstract class SyntaxyParser {

    private StringBuilder xmlOutput;

    private Parser parser;

    private VMWriter vmWriter;

    private SymbolTable symbolTable;

    public SyntaxyParser(Parser parser, SymbolTable symbolTable, VMWriter vmWriter,StringBuilder xmlOutput) {
        this.parser = parser;
        this.xmlOutput = xmlOutput;
        this.vmWriter = vmWriter;
        this.symbolTable = symbolTable;
    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    public VMWriter getVmWriter() {
        return vmWriter;
    }

    public Parser getParser() {
        return parser;
    }

    public String getClassName(){
        return parser.getClassName();
    }

    public void setClassName(String valor){
        parser.setClassName(valor);
    }

    public void setParser(Parser parser) {
        this.parser = parser;
    }

    Token getCurrentToken() {
        return parser.getCurrentToken();
    }

    TokenType getCurrentTokenType(){
        return parser.getCurrentToken().type;
    }

    String getPeekTokenLexeme(){
        return parser.getPeekToken().lexeme;
    }

    TokenType getPeekTokenType() {
        return parser.getPeekToken().type;
    }

    protected void expectPeek (TokenType type) {
        if (getPeekTokenType() == type ) {
            parser.nextToken();
            xmlOutput.append(String.format("%s\r\n", getCurrentToken().toString()));
        } else {
            throw new Error("Syntax error - expected "+type+" found " + getPeekTokenLexeme());
        }
    }

    protected void expectPeek(TokenType... types) {
        for (TokenType type : types) {
            if (getPeekTokenType() == type) {
                expectPeek(type);
                return;
            }
        }
        throw new Error("Syntax error ");
    }

    protected void printNonTerminal(String nterminal) {
        xmlOutput.append(String.format("<%s>\r\n", nterminal));
    }

}
