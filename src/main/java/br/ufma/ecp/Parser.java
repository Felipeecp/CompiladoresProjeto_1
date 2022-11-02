package br.ufma.ecp;

import br.ufma.ecp.parser.facade.FacadeSyntaxy;
import br.ufma.ecp.token.Token;

public class Parser {

    private Scanner scan;
    private Token currentToken;
    private Token peekToken;
    private FacadeSyntaxy facadeSyntaxy;

    public Parser (byte[] input) {
        scan = new Scanner(input);
        nextToken();
        facadeSyntaxy = new FacadeSyntaxy(this);
    }

    void parser () {
        facadeSyntaxy.parseClass();
    }

    public Token getPeekToken() {
        return peekToken;
    }

    public Token getCurrentToken() {
        return currentToken;
    }

    public void nextToken() {
        currentToken = peekToken;
        peekToken = scan.nextToken();
    }

    public String XMLOutput() {
        return facadeSyntaxy.XMLOutput();
    }

}
