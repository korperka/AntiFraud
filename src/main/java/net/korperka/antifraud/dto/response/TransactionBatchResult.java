package net.korperka.antifraud.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data @AllArgsConstructor
public class TransactionBatchResult {
    private List<TransactionBatchResultItem> items;
}
