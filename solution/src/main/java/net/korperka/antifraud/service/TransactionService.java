package net.korperka.antifraud.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import net.korperka.antifraud.dsl.parser.DslParser;
import net.korperka.antifraud.dsl.parser.RuleEvaluationContext;
import net.korperka.antifraud.dto.request.TransactionBatchCreateRequest;
import net.korperka.antifraud.dto.request.TransactionCreateRequest;
import net.korperka.antifraud.dto.response.*;
import net.korperka.antifraud.entity.FraudRule;
import net.korperka.antifraud.entity.Transaction;
import net.korperka.antifraud.enums.TransactionStatus;
import net.korperka.antifraud.exception.DateFormatException;
import net.korperka.antifraud.exception.NotFoundException;
import net.korperka.antifraud.mapper.TransactionMapper;
import net.korperka.antifraud.mapper.UserMapper;
import net.korperka.antifraud.repository.FraudRuleRepository;
import net.korperka.antifraud.repository.TransactionRepository;
import net.korperka.antifraud.repository.UserRepository;
import net.korperka.antifraud.specification.TransactionSpecification;
import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final FraudRuleRepository rulesRepository;
    private final TransactionMapper transactionMapper;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final Validator validator;
    private final ObjectMapper objectMapper;
    @Lazy
    private final TransactionService self;

    public TransactionBatchResult createBatch(TransactionBatchCreateRequest request) {
        List<TransactionBatchResultItem> results = new ArrayList<>();

        for (int i = 0; i < request.getItems().size(); i++) {
            try {
                TransactionCreateRequest itemRequest = objectMapper.treeToValue(request.getItems().get(i), TransactionCreateRequest.class);
                Set<ConstraintViolation<TransactionCreateRequest>> violations = validator.validate(itemRequest);

                if (!violations.isEmpty()) {
                    List<ApiErrorResponse.ValidationError> fieldErrors = violations.stream()
                            .map(v -> ApiErrorResponse.ValidationError.builder()
                                    .field(v.getPropertyPath().toString())
                                    .issue(v.getMessage())
                                    .rejectedValue(v.getInvalidValue())
                                    .build())
                            .toList();

                    ApiErrorResponse error = ApiErrorResponse.builder()
                            .code("VALIDATION_FAILED")
                            .message("Некоторые поля не прошли валидацию")
                            .fieldErrors(fieldErrors)
                            .timestamp(Instant.now())
                            .traceId(UUID.randomUUID().toString())
                            .build();

                    results.add(TransactionBatchResultItem.builder().index(i).error(error).build());
                    continue;
                }
                TransactionWrappedResponse response = self.createTransaction(itemRequest);

                results.add(TransactionBatchResultItem.builder()
                        .index(i)
                        .decision(response)
                        .build());

            }
            catch (JsonProcessingException | IllegalArgumentException e) {
                ApiErrorResponse error = ApiErrorResponse.builder()
                        .code("BAD_REQUEST")
                        .message(e.getMessage()) // Или getOriginalMessage если доступно
                        .timestamp(Instant.now())
                        .traceId(UUID.randomUUID().toString())
                        .build();

                results.add(TransactionBatchResultItem.builder().index(i).error(error).build());
            }
            catch (NotFoundException e) {
                ApiErrorResponse error = ApiErrorResponse.builder()
                        .code("NOT_FOUND")
                        .message(e.getMessage())
                        .timestamp(Instant.now())
                        .traceId(UUID.randomUUID().toString())
                        .build();

                results.add(TransactionBatchResultItem.builder().index(i).error(error).build());
            }
        }

        return new TransactionBatchResult(results);
    }

    private ApiErrorResponse mapExceptionToError(Exception e) {
        String code = "INTERNAL_ERROR";

        if (e instanceof NotFoundException) code = "NOT_FOUND";
        else if (e instanceof AccessDeniedException) code = "FORBIDDEN";
        else if (e instanceof HttpMessageNotReadableException) code = "BAD_REQUEST";

        return ApiErrorResponse.builder()
                .code(code)
                .message(e.getMessage())
                .timestamp(Instant.now())
                .build();
    }

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

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public TransactionWrappedResponse createTransaction(TransactionCreateRequest request) {
        Transaction transaction = transactionMapper.toEntity(request);

        UUID userId = request.getUserId();
        if (!userRepository.existsById(userId)) throw new NotFoundException();

        List<FraudRuleEvaluationResult> results = new ArrayList<>();
        List<FraudRule> rules = rulesRepository.findByEnabledTrue();
        rules.sort(Comparator.comparingInt(FraudRule::getPriority));

        RuleEvaluationContext context = RuleEvaluationContext.builder()
                .transaction(request)
                .user(userMapper.toDto(userRepository.findById(userId).orElseThrow(NotFoundException::new)))
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
        transaction.setStatus(fraud ? TransactionStatus.DECLINED : TransactionStatus.APPROVED);
        transaction.setRuleResults(results);
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setTimestamp(LocalDateTime.now());

        return transactionMapper.toDto(transactionRepository.save(transaction));
    }
}
