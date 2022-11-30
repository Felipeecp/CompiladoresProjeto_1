package br.ufma.ecp.parser;

import br.ufma.ecp.Parser;
import br.ufma.ecp.SymbolTable;
import br.ufma.ecp.VMWriter;
import br.ufma.ecp.parser.facade.FacadeSyntaxy;

import static br.ufma.ecp.token.TokenType.*;
import static br.ufma.ecp.token.TokenType.RBRACKET;

public class ClassDef extends SyntaxyParser{

    public ClassDef(Parser parser, SymbolTable symbolTable, VMWriter vmWriter, StringBuilder xmlOutput) {
        super(parser, symbolTable,vmWriter,xmlOutput);
    }

    public void parseClass(FacadeSyntaxy facadeSyntaxy) {
        printNonTerminal("class");
        expectPeek(CLASS);


        expectPeek(IDENTIFIER);
        setClassName(getCurrentTokeLexeme());

        expectPeek(LBRACKET);

        while (getPeekTokenType()==STATIC || getPeekTokenType() == FIELD){
            facadeSyntaxy.parseClassVarDec();
        }

        while(getPeekTokenType()==FUNCTION || getPeekTokenType()==CONSTRUCTOR ||getPeekTokenType()==METHOD){
            facadeSyntaxy.parseSubroutine();
        }
        expectPeek(RBRACKET);
        printNonTerminal("/class");
    }

}
