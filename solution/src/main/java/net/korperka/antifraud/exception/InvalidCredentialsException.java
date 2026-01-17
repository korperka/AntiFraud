package net.korperka.antifraud.exception;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {
        super("Невалидный JSON");
    }
}
