package net.korperka.antifraud.mapper;

import net.korperka.antifraud.dto.request.TransactionCreateRequest;
import net.korperka.antifraud.dto.response.TransactionResponse;
import net.korperka.antifraud.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    @Mapping(target = "transaction", source = ".")
    @Mapping(target = "ruleResults", source = "ruleResults")
    TransactionResponse toDto(Transaction transaction);
    Transaction toEntity(TransactionCreateRequest dto);
}
