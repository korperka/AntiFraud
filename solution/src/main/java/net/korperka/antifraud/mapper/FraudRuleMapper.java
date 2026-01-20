package net.korperka.antifraud.mapper;

import net.korperka.antifraud.dto.request.FraudRuleDTO;
import net.korperka.antifraud.dto.response.FraudRuleResponseDTO;
import net.korperka.antifraud.entity.FraudRule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FraudRuleMapper {
    FraudRuleResponseDTO toDto(FraudRule entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    FraudRule toEntity(FraudRuleDTO dto);
}
