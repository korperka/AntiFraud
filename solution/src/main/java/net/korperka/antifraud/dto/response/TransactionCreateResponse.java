package net.korperka.antifraud.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.korperka.antifraud.entity.Transaction;

import java.util.List;

@Data
@AllArgsConstructor
public class TransactionCreateResponse {
    private Transaction transaction;
    private List<FraudRuleEvaluationResult> ruleResults;
}
