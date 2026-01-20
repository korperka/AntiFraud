package net.korperka.antifraud.dto.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FraudRuleDTO {
    @NotBlank @Size(min = 3, max = 120)
    private String name;
    @Size(max = 500)
    private String description;
    @Size(min = 3, max = 2000)
    private String dslExpression;

    private boolean enabled;
    @Min(1)
    private int priority;
}
