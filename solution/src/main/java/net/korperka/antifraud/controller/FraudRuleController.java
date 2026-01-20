package net.korperka.antifraud.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.korperka.antifraud.dto.request.FraudRuleDTO;
import net.korperka.antifraud.dto.response.FraudRuleResponseDTO;
import net.korperka.antifraud.service.FraudRuleService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/fraud_rules")
@Tag(name = "FraudRules", description = "Управление правилами фрода")
public class FraudRuleController {
    private final FraudRuleService ruleService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FraudRuleResponseDTO> createFraudRule(@Valid @RequestBody FraudRuleDTO request) {
        return ResponseEntity.status(201).body(ruleService.createFraudRule(request));
    }
}
