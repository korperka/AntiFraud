package net.korperka.antifraud.exception;

//TODO отдельно для правил?
public class NotFoundException extends RuntimeException {
    public NotFoundException() {
        super("Ресурс не найден");
    }
}
