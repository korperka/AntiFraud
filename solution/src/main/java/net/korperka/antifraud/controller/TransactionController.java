package net.korperka.antifraud.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import net.korperka.antifraud.dto.request.TransactionCreateRequest;
import net.korperka.antifraud.dto.response.TransactionListResponse;
import net.korperka.antifraud.dto.response.TransactionWrappedResponse;
import net.korperka.antifraud.enums.TransactionStatus;
import net.korperka.antifraud.service.TransactionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/transactions")
@Tag(name = "Transactions", description = "Транзакции (покупки), запуск антифрода")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TransactionListResponse> getTransactions(
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) TransactionStatus status,
            @RequestParam(required = false) Boolean isFraud,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) int size,
            Authentication authentication
    ) {
        UUID currentUserId = UUID.fromString(authentication.getName());

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        return ResponseEntity.ok(
                transactionService.getTransactions(userId, status, isFraud, from, to, page, size, currentUserId, isAdmin)
        );
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<TransactionWrappedResponse> createTransaction(@Valid @RequestBody TransactionCreateRequest request) {
        return ResponseEntity.status(201).body(transactionService.createTransaction(request));
    }
}
