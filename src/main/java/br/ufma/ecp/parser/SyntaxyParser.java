package br.ufma.ecp.parser;

import br.ufma.ecp.Parser;
import br.ufma.ecp.VMWriter;
import br.ufma.ecp.token.Token;
import br.ufma.ecp.token.TokenType;

public abstract class SyntaxyParser {

    private StringBuilder xmlOutput = new StringBuilder();
    private VMWriter vmWrite = new VMWriter();

    private Parser parser;

    public SyntaxyParser(Parser parser, StringBuilder xmlOutput, VMWriter vmWrite) {
        this.parser = parser;
        this.xmlOutput = xmlOutput;
        this.vmWrite = vmWrite;
    }

    Token getPeekToken(){
        return parser.getPeekToken();
    }

    Token getCurrentToken() {
        return parser.getCurrentToken();
    }

    TokenType getTokenType() {
        return parser.getPeekToken().type;
    }

    protected void expectPeek (TokenType type) {
        if (getPeekToken().type == type ) {
            parser.nextToken();
            xmlOutput.append(String.format("%s\r\n", getCurrentToken().toString()));
        } else {
            throw new Error("Syntax error - expected "+type+" found " + getPeekToken().lexeme);
        }
    }

    protected void expectPeek(TokenType... types) {
        for (TokenType type : types) {
            if (getPeekToken().type == type) {
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
