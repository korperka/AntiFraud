package net.korperka.antifraud.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.korperka.antifraud.dto.request.TransactionLocationDTO;
import net.korperka.antifraud.enums.TransactionChannel;
import net.korperka.antifraud.enums.TransactionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
public class TransactionResponseDTO {
    private UUID id;
    private UUID userId;
    private BigDecimal amount;
    private String currency;
    private TransactionStatus status;
    private String merchantId;
    private String merchantCategoryCode;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime timestamp;

    private String ipAddress;
    private String deviceId;
    private TransactionChannel channel;
    private TransactionLocationDTO location;

    @JsonProperty("isFraud")
    private boolean fraud;

    private Map<String, Object> metadata;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime createdAt;

}
