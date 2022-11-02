package br.ufma.ecp.parser;

import br.ufma.ecp.Parser;
import br.ufma.ecp.token.Token;
import br.ufma.ecp.token.TokenType;

public abstract class SyntaxyParser {

    private StringBuilder xmlOutput;

    private Parser parser;

    public SyntaxyParser(Parser parser, StringBuilder xmlOutput) {
        this.parser = parser;
        this.xmlOutput = xmlOutput;
    }

    Token getCurrentToken() {
        return parser.getCurrentToken();
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
