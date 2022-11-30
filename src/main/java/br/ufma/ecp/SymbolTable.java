package br.ufma.ecp;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {

    public static record Symbol(String name, String type, Kind kind, int index) {
    }

    private Map<String, Symbol> classScope;
    private Map<String, Symbol> subroutineScope;
    private Map<Kind, Integer> countVars;

    public SymbolTable() {
        classScope = new HashMap<>();
        subroutineScope = new HashMap<>();
        countVars = new HashMap<>();

        countVars.put(Kind.ARG, 0);
        countVars.put(Kind.VAR, 0);
        countVars.put(Kind.STATIC, 0);
        countVars.put(Kind.FIELD, 0);

    }

    public void startSubroutine() {

        subroutineScope.clear();
        countVars.put(Kind.ARG, 0);
        countVars.put(Kind.VAR, 0);


    }

    private Map<String,Symbol> scope (Kind kind) {
        if (kind == Kind.STATIC || kind == Kind.FIELD){
            return classScope;
        } else {
            return subroutineScope;
        }
    }

    public void define(String name, String type, Kind kind) {

        Symbol s = new Symbol (name, type, kind, varCount(kind) );
        if (kind == Kind.STATIC || kind == Kind.FIELD) {
            classScope.put(name, s);
        } else {
            subroutineScope.put(name, s);
        }
        countVars.put(kind, countVars.get(kind) + 1);

    }

    public Symbol resolve (String name) {
        Symbol s = subroutineScope.get(name);
        if (s != null) return s;
        else return classScope.get(name);

    }

    public int varCount(Kind kind) {
        return countVars.get(kind);
    }

}
