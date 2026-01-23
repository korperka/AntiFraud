package net.korperka.antifraud.exception;

public class InvalidFieldException extends RuntimeException {
    public InvalidFieldException() {
        super("DSL_INVALID_FIELD");
    }
}
