package net.korperka.antifraud.dsl.token;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Token {
    private TokenType type;
    private String value;
    private int position;
}
