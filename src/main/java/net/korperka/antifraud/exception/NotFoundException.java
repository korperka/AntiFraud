package net.korperka.antifraud.exception;

import lombok.Getter;

import java.util.UUID;

//TODO отдельно для правил?
@Getter
public class NotFoundException extends RuntimeException {
    private final UUID userId;

    public NotFoundException(UUID userId) {
        super("Ресурс не найден");
        this.userId = userId;
    }
}
