package net.korperka.antifraud.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.korperka.antifraud.service.TransactionService;

import java.util.List;

@Data
@AllArgsConstructor
public class TransactionListResponse {
    private List<TransactionResponse> items;
    private Integer total;
    private Integer page;
    private Integer size;
}
