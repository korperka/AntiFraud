package net.korperka.antifraud.service;

import lombok.RequiredArgsConstructor;
import net.korperka.antifraud.dsl.DslError;
import net.korperka.antifraud.dsl.node.Node;
import net.korperka.antifraud.dsl.parser.DslParser;
import net.korperka.antifraud.dto.request.DslValidateRequest;
import net.korperka.antifraud.dto.request.FraudRuleDTO;
import net.korperka.antifraud.dto.response.DslValidateResponse;
import net.korperka.antifraud.dto.response.FraudRuleResponse;
import net.korperka.antifraud.entity.FraudRule;
import net.korperka.antifraud.exception.DslParseException;
import net.korperka.antifraud.exception.FraudRuleAlreadyExistsException;
import net.korperka.antifraud.exception.NotFoundException;
import net.korperka.antifraud.mapper.FraudRuleMapper;
import net.korperka.antifraud.repository.FraudRuleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FraudRuleService {
    private final FraudRuleRepository rulesRepository;
    private final FraudRuleMapper rulesMapper;

    public DslValidateResponse validateExpression(DslValidateRequest request) {
        String dslExpression = request.getDslExpression();
        List<DslError> errors = new ArrayList<>();
        boolean valid = true;

        try {
            new DslParser(dslExpression).parse();
        } catch (DslParseException e) {
            errors.add(new DslError(e.getMessage(), e.getMessage(), e.getNear(), e.getPosition()));
            valid = false;
        }
        catch(Exception e) {
            errors.add(new DslError(e.getMessage(), e.getMessage(), null, null));
            valid = false;
        }

        return new DslValidateResponse(valid, DslParser.normalizeExpressionSafe(dslExpression), errors);
    }

    public FraudRuleResponse disableRule(UUID ruleId) {
        FraudRule rule = rulesRepository.findById(ruleId).orElseThrow(() -> new NotFoundException(ruleId));
        rule.setEnabled(false);

        return rulesMapper.toDto(rulesRepository.save(rule));
    }

    public FraudRuleResponse updateRule(FraudRuleDTO sourceDTO, UUID targetId) {
        FraudRule target = rulesRepository.findById(targetId).orElseThrow(() -> new NotFoundException(targetId));
        FraudRule source = rulesMapper.toEntity(sourceDTO);

        if(rulesRepository.existsByNameAndIdNot(source.getName(), source.getId())) throw new FraudRuleAlreadyExistsException();

        target.setName(source.getName());
        target.setDescription(source.getDescription());
        target.setDslExpression(DslParser.normalizeExpressionSafe(source.getDslExpression()));
        target.setEnabled(source.isEnabled());
        target.setPriority(source.getPriority());
        target.setUpdatedAt(LocalDateTime.now());

        return rulesMapper.toDto(rulesRepository.save(target));
    }

    public FraudRuleResponse getRuleById(UUID id) {
        return rulesMapper.toDto(rulesRepository.findById(id).orElseThrow(() -> new NotFoundException(id)));
    }

    public List<FraudRuleResponse> getAllRules() {
        return rulesRepository.findAll().stream().map(rulesMapper::toDto).toList();
    }

    public FraudRuleResponse createFraudRule(FraudRuleDTO request) {
        if(rulesRepository.existsByName(request.getName())) throw new FraudRuleAlreadyExistsException();

        FraudRule rule = rulesMapper.toEntity(request);

        rule.setCreatedAt(LocalDateTime.now());
        rule.setUpdatedAt(LocalDateTime.now());
        rule.setDslExpression(DslParser.normalizeExpressionSafe(request.getDslExpression()));

        return rulesMapper.toDto(rulesRepository.save(rule));
    }
}
