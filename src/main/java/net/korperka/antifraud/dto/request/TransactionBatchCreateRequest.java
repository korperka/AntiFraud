package net.korperka.antifraud.dto.request;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class TransactionBatchCreateRequest {
    @NotNull
    private List<JsonNode> items;
}
