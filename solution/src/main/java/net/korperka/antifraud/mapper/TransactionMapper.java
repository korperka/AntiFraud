package net.korperka.antifraud.mapper;

import net.korperka.antifraud.dto.request.TransactionCreateRequest;
import net.korperka.antifraud.dto.response.TransactionResponseDTO;
import net.korperka.antifraud.dto.response.TransactionWrappedResponse;
import net.korperka.antifraud.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    TransactionResponseDTO toTransactionDto(Transaction transaction);

    @Mapping(target = "transaction", source = ".")
    @Mapping(target = "ruleResults", source = "ruleResults")
    TransactionWrappedResponse toDto(Transaction transaction);

    @Mapping(target = "id", ignore = true)
    Transaction toEntity(TransactionCreateRequest request);
}
