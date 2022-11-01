package br.ufma.ecp.parser.facade;

import br.ufma.ecp.Parser;
import br.ufma.ecp.VMWriter;
import br.ufma.ecp.parser.ClassDef;
import br.ufma.ecp.parser.ClassVardec;
import br.ufma.ecp.parser.SubroutineDec;

public class FacadeSyntaxy {

    private StringBuilder xmlOutput;
    private ClassDef classDef;
    private ClassVardec classVardec;
    private SubroutineDec subroutineDec;
    private VMWriter vmWriter;

    public FacadeSyntaxy(Parser parser) {
        xmlOutput = new StringBuilder();
        classDef = new ClassDef(parser, xmlOutput);
        classVardec = new ClassVardec(parser, xmlOutput, vmWriter);
        subroutineDec = new SubroutineDec(parser, xmlOutput, vmWriter);
    }

    public void parseClass() {
        classDef.parseClass(this);
    }

    public void parseClassVarDec() {
        classVardec.parseClassVarDec();
    }

    public void parseSubroutine(){
        subroutineDec.parseSubroutineDec();
    }

    public String XMLOutput() {
        return xmlOutput.toString();
    }

    public String VMOutput(){return vmWriter.vmOutput();}

}
