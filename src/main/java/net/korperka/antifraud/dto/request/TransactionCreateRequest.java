package net.korperka.antifraud.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.korperka.antifraud.enums.TransactionChannel;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

//TODO check currency?
@Data
@AllArgsConstructor
public class TransactionCreateRequest {
    @NotNull
    private UUID userId;

    @DecimalMin(value="0.01") @DecimalMax(value = "999999999.99")
    private double amount;

    @NotNull @Pattern(regexp = "^[A-Z]{3}$")
    private String currency;

    private String merchantId;
    @Pattern(regexp = "^\\d{4}$")
    private String merchantCategoryCode;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime timestamp;

    @Size(max = 64)
    private String ipAddress;
    @Size(max = 128)
    private String deviceId;

    private TransactionChannel channel;
    @Valid
    private TransactionLocationDTO location;

    private Map<String, Object> metadata;
}
