package net.korperka.antifraud.service;

import lombok.RequiredArgsConstructor;
import net.korperka.antifraud.dto.request.FraudRuleDTO;
import net.korperka.antifraud.dto.response.FraudRuleResponseDTO;
import net.korperka.antifraud.entity.FraudRule;
import net.korperka.antifraud.exception.FraudRuleAlreadyExistsException;
import net.korperka.antifraud.exception.NotFoundException;
import net.korperka.antifraud.mapper.FraudRuleMapper;
import net.korperka.antifraud.repository.FraudRuleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FraudRuleService {
    private final FraudRuleRepository rulesRepository;
    private final FraudRuleMapper rulesMapper;

    public FraudRuleResponseDTO disableRule(UUID ruleId) {
        FraudRule rule = rulesRepository.findById(ruleId).orElseThrow(NotFoundException::new);
        rule.setEnabled(false);

        return rulesMapper.toDto(rulesRepository.save(rule));
    }

    public FraudRuleResponseDTO updateRule(FraudRuleDTO sourceDTO, UUID targetId) {
        FraudRule target = rulesRepository.findById(targetId).orElseThrow(NotFoundException::new);
        FraudRule source = rulesMapper.toEntity(sourceDTO);

        if(rulesRepository.existsByName(source.getName())) throw new FraudRuleAlreadyExistsException();

        target.setName(source.getName());
        target.setDescription(source.getDescription());
        target.setDslExpression(source.getDslExpression());
        target.setEnabled(source.isEnabled());
        target.setPriority(source.getPriority());
        target.setUpdatedAt(LocalDateTime.now());

        return rulesMapper.toDto(rulesRepository.save(target));
    }

    public FraudRuleResponseDTO getRuleById(UUID id) {
        return rulesMapper.toDto(rulesRepository.findById(id).orElseThrow(NotFoundException::new));
    }

    public List<FraudRuleResponseDTO> getAllRules() {
        return rulesRepository.findAll().stream().map(rulesMapper::toDto).toList();
    }

    public FraudRuleResponseDTO createFraudRule(FraudRuleDTO request) {
        if(rulesRepository.existsByName(request.getName())) throw new FraudRuleAlreadyExistsException();

        FraudRule rule = rulesMapper.toEntity(request);

        rule.setCreatedAt(LocalDateTime.now());
        rule.setUpdatedAt(LocalDateTime.now());

        return rulesMapper.toDto(rulesRepository.save(rule));
    }
}
