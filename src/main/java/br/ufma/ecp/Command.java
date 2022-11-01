package br.ufma.ecp;

import br.ufma.ecp.token.TokenType;

import static br.ufma.ecp.token.TokenType.MINUS;
import static br.ufma.ecp.token.TokenType.PLUS;

public enum Command {
    ADD,
    SUB,
    NEG,
    EQ,
    GT,
    LT,
    AND,
    OR,
    NOT;

    public static Command typeOperator(TokenType type) {
        switch (type){
            case PLUS -> {
                return Command.ADD;
            }
            case MINUS -> {
                return Command.SUB;
            }
            case LT -> {
                return Command.LT;
            }
            case GT -> {
                return Command.GT;
            }
            case EQ -> {
                return Command.EQ;
            }
            case AND -> {
                return Command.AND;
            }
            case OR -> {
                return Command.OR;
            }
            default -> {
                return null;
            }
        }
    }
}


