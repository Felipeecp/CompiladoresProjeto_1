package br.ufma.ecp.parser.facade;

import br.ufma.ecp.Parser;
import br.ufma.ecp.parser.ClassDef;
import br.ufma.ecp.parser.ClassVardec;
import br.ufma.ecp.parser.SubroutineDec;

public class FacadeSyntaxy {

    private StringBuilder xmlOutput;
    private ClassDef classDef;
    private ClassVardec classVardec;
    private SubroutineDec subroutineDec;

    public FacadeSyntaxy(Parser parser) {
        xmlOutput = new StringBuilder();
        classDef = new ClassDef(parser, xmlOutput);
        classVardec = new ClassVardec(parser, xmlOutput);
        subroutineDec = new SubroutineDec(parser, xmlOutput);
    }

    public void parseClass() {
        classDef.parseClass(this);
    }

    public void parseClassVarDec() {
        classVardec.parseClassVarDec();
    }

    public void parseSubroutine(){
        subroutineDec.varMethod();
    }

    public void parserLet(){
        subroutineDec.parseLet();
    }

    public void parseIf(){
        subroutineDec.parseIf();
    }

    public void parseDo(){
        subroutineDec.parseDo();
    }

    public String XMLOutput() {
        return xmlOutput.toString();
    }


}
