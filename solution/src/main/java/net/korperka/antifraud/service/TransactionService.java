package net.korperka.antifraud.service;

import jakarta.validation.ConstraintDeclarationException;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import net.korperka.antifraud.dsl.parser.DslParser;
import net.korperka.antifraud.dsl.parser.RuleEvaluationContext;
import net.korperka.antifraud.dto.request.TransactionCreateRequest;
import net.korperka.antifraud.dto.response.FraudRuleEvaluationResult;
import net.korperka.antifraud.dto.response.TransactionListResponse;
import net.korperka.antifraud.dto.response.TransactionResponse;
import net.korperka.antifraud.entity.FraudRule;
import net.korperka.antifraud.entity.Transaction;
import net.korperka.antifraud.enums.TransactionStatus;
import net.korperka.antifraud.mapper.TransactionMapper;
import net.korperka.antifraud.repository.FraudRuleRepository;
import net.korperka.antifraud.repository.TransactionRepository;
import net.korperka.antifraud.specification.TransactionSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.validation.method.MethodValidationResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final FraudRuleRepository rulesRepository;
    private final TransactionMapper transactionMapper;

    public TransactionListResponse getTransactions(
            UUID filterUserId,
            TransactionStatus status,
            Boolean fraud,
            LocalDateTime from,
            LocalDateTime to,
            int page,
            int size,
            UUID currentUserId,
            boolean isAdmin
    ) {
        if (to == null)
            to = LocalDateTime.now();
        if (from == null)
            from = to.minusDays(90);

        if (!isAdmin) {
            if (filterUserId != null && !filterUserId.equals(currentUserId)) throw new AccessDeniedException("Forbidden");

            filterUserId = currentUserId;
        }
        Specification<Transaction> spec = TransactionSpecification.filter(filterUserId, status, fraud, from, to);
        Pageable pageable = PageRequest.of(page, size);
        Page<Transaction> transactionPage = transactionRepository.findAll(spec, pageable);

        if(from != null && from.isAfter(to)) throw new HandlerMethodValidationException(MethodValidationResult.emptyResult());
        if(from != null && ChronoUnit.DAYS.between(from, to) > 90) throw new HandlerMethodValidationException(MethodValidationResult.emptyResult());

        List<TransactionResponse> content = transactionPage.getContent().stream()
                .map(transactionMapper::toDto)
                .toList();

        return new TransactionListResponse(
                content,
                (int) transactionPage.getTotalElements(),
                transactionPage.getNumber(),
                transactionPage.getSize()
        );
    }

    public TransactionResponse createTransaction(TransactionCreateRequest request) {
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
        transaction.setRuleResults(results);

        return transactionMapper.toDto(transactionRepository.save(transaction));
    }
}
