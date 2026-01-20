package net.korperka.antifraud.service;

import lombok.RequiredArgsConstructor;
import net.korperka.antifraud.dto.request.FraudRuleDTO;
import net.korperka.antifraud.dto.response.FraudRuleResponseDTO;
import net.korperka.antifraud.entity.FraudRule;
import net.korperka.antifraud.exception.FraudRuleAlreadyExistsException;
import net.korperka.antifraud.exception.UserAlreadyExistsException;
import net.korperka.antifraud.mapper.FraudRuleMapper;
import net.korperka.antifraud.repository.FraudRuleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FraudRuleService {
    private final FraudRuleRepository rulesRepository;
    private final FraudRuleMapper rulesMapper;

    public FraudRuleResponseDTO createFraudRule(FraudRuleDTO request) {
        if(rulesRepository.existsByName(request.getName())) throw new FraudRuleAlreadyExistsException();

        FraudRule rule = rulesMapper.toEntity(request);

        rule.setCreatedAt(LocalDateTime.now());
        rule.setUpdatedAt(LocalDateTime.now());

        return rulesMapper.toDto(rulesRepository.save(rule));
    }
}
