package br.ufma.ecp;

public enum Segment{
    CONST("constant"),
    ARG("argument"),
    LOCAL("static"),
    STATIC("static"),
    THIS("this"),
    THAT("that"),
    POINTER("pointer"),
    TEMP("temp");

    public String value;
    private Segment(String value) {
        this.value = value;
    }
}