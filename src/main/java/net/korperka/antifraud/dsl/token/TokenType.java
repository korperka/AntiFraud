package net.korperka.antifraud.dsl.token;

public enum TokenType {
    FIELD,
    COMPARE_OP,
    LOGICAL_OP,
    NOT_OP,

    STRING,
    NUMBER,

    L_BRACKET,
    R_BRACKET,

    EOF
}
