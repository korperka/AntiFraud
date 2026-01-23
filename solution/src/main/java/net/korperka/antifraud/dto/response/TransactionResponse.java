package net.korperka.antifraud.dto.response;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.korperka.antifraud.entity.Transaction;

import java.util.List;

@Data
@AllArgsConstructor
public class TransactionResponse {
    @JsonUnwrapped
    private Transaction transaction;
    private List<FraudRuleEvaluationResult> ruleResults;
}
