package net.korperka.antifraud.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DslValidateRequest {
    @Size(min = 3, max = 2000)
    private String dslExpression;
}
