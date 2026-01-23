package net.korperka.antifraud.service;

import lombok.RequiredArgsConstructor;
import net.korperka.antifraud.dsl.parser.DslParser;
import net.korperka.antifraud.dsl.parser.RuleEvaluationContext;
import net.korperka.antifraud.dto.request.TransactionCreateRequest;
import net.korperka.antifraud.dto.response.FraudRuleEvaluationResult;
import net.korperka.antifraud.dto.response.TransactionListResponse;
import net.korperka.antifraud.dto.response.TransactionResponseDTO;
import net.korperka.antifraud.dto.response.TransactionWrappedResponse;
import net.korperka.antifraud.entity.FraudRule;
import net.korperka.antifraud.entity.Transaction;
import net.korperka.antifraud.enums.TransactionStatus;
import net.korperka.antifraud.exception.DateFormatException;
import net.korperka.antifraud.exception.NotFoundException;
import net.korperka.antifraud.mapper.TransactionMapper;
import net.korperka.antifraud.repository.FraudRuleRepository;
import net.korperka.antifraud.repository.TransactionRepository;
import net.korperka.antifraud.repository.UserRepository;
import net.korperka.antifraud.specification.TransactionSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

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
    private final UserRepository userRepository;

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
        if (to == null) to = LocalDateTime.now();
        if (from == null) from = to.minusDays(90);
        if (!isAdmin) filterUserId = currentUserId;
        if(from.isAfter(to)) throw new DateFormatException();
        if(ChronoUnit.DAYS.between(from, to) > 90) throw new DateFormatException();

        Specification<Transaction> spec = TransactionSpecification.filter(filterUserId, status, fraud, from, to);
        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
        Page<Transaction> transactionPage = transactionRepository.findAll(spec, pageable);

        List<TransactionResponseDTO> content = transactionPage.getContent().stream()
                .map(transactionMapper::toTransactionDto)
                .toList();

        return new TransactionListResponse(
                content,
                (int) transactionPage.getTotalElements(),
                transactionPage.getNumber(),
                transactionPage.getSize()
        );
    }

    public TransactionWrappedResponse createTransaction(TransactionCreateRequest request) {
        Transaction transaction = transactionMapper.toEntity(request);

        if (!userRepository.existsById(request.getUserId())) throw new NotFoundException();

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
