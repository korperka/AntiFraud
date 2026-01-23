package net.korperka.antifraud.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class FraudRuleEvaluationResult {
    private UUID ruleId;
    private String ruleName;
    private int priority;
    private boolean enabled;
    private boolean matched;
    private String description;
}
