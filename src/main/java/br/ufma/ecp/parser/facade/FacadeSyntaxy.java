package br.ufma.ecp.parser.facade;

import br.ufma.ecp.Parser;
import br.ufma.ecp.SymbolTable;
import br.ufma.ecp.VMWriter;
import br.ufma.ecp.parser.ClassDef;
import br.ufma.ecp.parser.ClassVardec;
import br.ufma.ecp.parser.SubroutineDec;

// TODO -> MELHORAR
public class FacadeSyntaxy {

    private StringBuilder xmlOutput;
    private ClassDef classDef;
    private ClassVardec classVardec;
    private SubroutineDec subroutineDec;

    private SymbolTable symbolTable;

    private VMWriter vmWriter;


    public FacadeSyntaxy(Parser parser) {
        xmlOutput = new StringBuilder();
        symbolTable = new SymbolTable();
        vmWriter = new VMWriter();
        classDef = new ClassDef(parser, symbolTable,vmWriter,xmlOutput);
        classVardec = new ClassVardec(parser, symbolTable,vmWriter,xmlOutput);
        subroutineDec = new SubroutineDec(parser, symbolTable,vmWriter,xmlOutput);
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

    public String vmOutput(){return vmWriter.vmOutput();}

}
