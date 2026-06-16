package net.korperka.antifraud.exception;

public class InvalidOperatorException extends RuntimeException {
    public InvalidOperatorException() {
        super("DSL_INVALID_OPERATOR");
    }
}
