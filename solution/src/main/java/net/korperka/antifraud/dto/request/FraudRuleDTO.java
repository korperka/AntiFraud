package net.korperka.antifraud.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FraudRuleDTO {
    @NotBlank @Size(min = 3, max = 120)
    private String name;
    @Size(max = 500)
    private String description;
    @NotBlank @Size(min = 3, max = 2000)
    private String dslExpression;

    private Boolean enabled = true;
    @NotNull @Min(1)
    private Integer priority;
}
