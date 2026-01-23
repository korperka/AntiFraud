package net.korperka.antifraud.mapper;

import net.korperka.antifraud.dto.request.TransactionCreateRequest;
import net.korperka.antifraud.dto.response.TransactionResponse;
import net.korperka.antifraud.entity.Transaction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    TransactionResponse toDto(Transaction transaction);
    Transaction toEntity(TransactionCreateRequest dto);
}
