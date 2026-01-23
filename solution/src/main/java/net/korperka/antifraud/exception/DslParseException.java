package net.korperka.antifraud.exception;

import lombok.Getter;

@Getter
public class DslParseException extends RuntimeException {
    private final int position;
    private final String near;

    public DslParseException(int position, String near) {
        super("DSL_PARSE_ERROR");
        this.position = position;
        this.near = near;
    }
}
