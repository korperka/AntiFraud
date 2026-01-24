package net.korperka.antifraud.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionBatchResultItem {
    private Integer index;
    private TransactionWrappedResponse decision;
    private ApiErrorResponse error;
}
