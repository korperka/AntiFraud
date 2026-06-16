package net.korperka.antifraud.dsl;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DslError {
    private String code;
    private String message;
    private String near;

    private Integer position;
}
