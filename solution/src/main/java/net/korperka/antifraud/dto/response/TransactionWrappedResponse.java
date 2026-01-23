package net.korperka.antifraud.dto.response;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TransactionWrappedResponse {
    @JsonUnwrapped
    private TransactionResponseDTO transaction;
    private List<FraudRuleEvaluationResult> ruleResults;
}
