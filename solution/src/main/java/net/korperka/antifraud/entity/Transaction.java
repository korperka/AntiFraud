package net.korperka.antifraud.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import net.korperka.antifraud.dto.request.TransactionLocationDTO;
import net.korperka.antifraud.dto.response.FraudRuleEvaluationResult;
import net.korperka.antifraud.enums.TransactionChannel;
import net.korperka.antifraud.enums.TransactionStatus;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Data
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;

    @Column(length = 64)
    private String merchantId;

    @Column(length = 4)
    private String merchantCategoryCode;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(length = 64)
    private String ipAddress;

    @Column(length = 128)
    private String deviceId;

    @Enumerated(EnumType.STRING)
    private TransactionChannel channel;

    @Embedded
    private TransactionLocationDTO location;

    @Column(nullable = false)
    @JsonProperty("isFraud")
    private boolean fraud;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> metadata;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<FraudRuleEvaluationResult> ruleResults;
}