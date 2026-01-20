package net.korperka.antifraud.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.korperka.antifraud.dto.request.FraudRuleDTO;
import net.korperka.antifraud.dto.response.FraudRuleResponseDTO;
import net.korperka.antifraud.service.FraudRuleService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/fraud_rules")
@Tag(name = "FraudRules", description = "Управление правилами фрода")
public class FraudRuleController {
    private final FraudRuleService ruleService;

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FraudRuleResponseDTO> disableFraudRule(@PathVariable UUID id) {
        ruleService.disableRule(id);

        return ResponseEntity.status(204).build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FraudRuleResponseDTO> updateFraudRule(@Valid @RequestBody FraudRuleDTO source, UUID targetId) {
        return ResponseEntity.ok(ruleService.updateRule(source, targetId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FraudRuleResponseDTO> getFraudRule(@PathVariable UUID id) {
        return ResponseEntity.ok(ruleService.getRuleById(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FraudRuleResponseDTO>> getAllFraudRules() {
        return ResponseEntity.ok(ruleService.getAllRules());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FraudRuleResponseDTO> createFraudRule(@Valid @RequestBody FraudRuleDTO request) {
        return ResponseEntity.status(201).body(ruleService.createFraudRule(request));
    }
}
