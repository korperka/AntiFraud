package net.korperka.antifraud.service;

import lombok.RequiredArgsConstructor;
import net.korperka.antifraud.dsl.parser.DslParser;
import net.korperka.antifraud.dsl.parser.RuleEvaluationContext;
import net.korperka.antifraud.dto.request.TransactionCreateRequest;
import net.korperka.antifraud.dto.response.FraudRuleEvaluationResult;
import net.korperka.antifraud.dto.response.TransactionCreateResponse;
import net.korperka.antifraud.entity.FraudRule;
import net.korperka.antifraud.entity.Transaction;
import net.korperka.antifraud.enums.TransactionStatus;
import net.korperka.antifraud.mapper.TransactionMapper;
import net.korperka.antifraud.repository.FraudRuleRepository;
import net.korperka.antifraud.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final FraudRuleRepository rulesRepository;
    private final TransactionMapper transactionMapper;

    public TransactionCreateResponse createTransaction(TransactionCreateRequest request) {
        Transaction transaction = transactionMapper.toEntity(request);

        List<FraudRuleEvaluationResult> results = new ArrayList<>();
        List<FraudRule> rules = rulesRepository.findByEnabledTrue();
        rules.sort(Comparator.comparingInt(FraudRule::getPriority));

        RuleEvaluationContext context = RuleEvaluationContext.builder()
                .transaction(request)
                .build();

        boolean fraud = false;
        for (FraudRule rule : rules) {
            boolean matched = false;
            try {
                matched = new DslParser(rule.getDslExpression()).parse().evaluate(context);
            } catch (Exception ignored) { }

            if (matched) fraud = true;

            FraudRuleEvaluationResult result = new FraudRuleEvaluationResult(rule.getId(), rule.getName(), rule.getPriority(), rule.isEnabled(), matched, rule.getDescription());
            results.add(result);
        }

        transaction.setFraud(fraud);
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setStatus(fraud ? TransactionStatus.DECLINED : TransactionStatus.APPROVED);

        TransactionCreateResponse response = transactionMapper.toDto(transactionRepository.save(transaction));
        response.setRuleResults(results);

        return response;
    }
}
