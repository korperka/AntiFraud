package net.korperka.antifraud.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.korperka.antifraud.dsl.DslError;

import java.util.List;

@Data
@AllArgsConstructor
public class DslValidateResponse {
    @JsonProperty("isValid")
    private boolean valid;
    private String normalizedExpression;
    private List<DslError> errors;
}
