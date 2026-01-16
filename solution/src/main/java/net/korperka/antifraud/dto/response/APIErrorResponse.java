package net.korperka.antifraud.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class APIErrorResponse {
    private String code;
    private String message;
    private String traceId;
    private Instant timestamp;
    private String path;

    private Map<String, Object> details;
    private List<ValidationError> fieldErrors;

    @Data
    @Builder
    public static class ValidationError {
        private String field;
        private String issue;
        private Object rejectedValue;
    }
}