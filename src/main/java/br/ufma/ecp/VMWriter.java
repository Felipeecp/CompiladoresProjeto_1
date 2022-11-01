package br.ufma.ecp;

public class VMWriter {

    private StringBuilder vmOutput = new StringBuilder();

    public String vmOutput(){
        return vmOutput.toString();
    }

    public void writePush(Segment segment, int index){
        vmOutput.append(String.format("push %s %d\n",segment.value, index));
    }

    public void writePop(Segment segment, int index){
        vmOutput.append(String.format("pop %s %d\n", segment.value, index));
    }

    public void writeArithmetic(Command command){
        vmOutput.append(String.format("%s\n",command.name().toLowerCase()));
    }

    public void writeLabel(String label){
        vmOutput.append(String.format("label %s\n", label));
    }

    public void writeGoto(String label){
        vmOutput.append(String.format("goto %s\n", label));
    }

    public void writeCall(String name, int nArgs){
        vmOutput.append(String.format("call %s %d\b", name, nArgs));
    }

    public void writeFunction(String name, int nLocals){
        vmOutput.append(String.format("function %s %d\n", name, nLocals));
    }

    public void writeReturn(){
        vmOutput.append(String.format("return\n"));
    }

}
