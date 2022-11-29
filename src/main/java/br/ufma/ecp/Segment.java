package br.ufma.ecp;

public enum Segment {
    CONST("constant"),
    ARG("argument"),
    LOCAL("local"),
    STATIC("static"),
    THIS("this"),
    THAT("that"),
    POINTER("pointer"),
    TEMP("temp");

    private Segment(String value) {
        this.value = value;
    }

    public String value;
}