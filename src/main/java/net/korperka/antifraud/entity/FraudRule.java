package net.korperka.antifraud.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "fraud_rules")
@Data
public class FraudRule {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(nullable = false, length = 2000)
    private String dslExpression;

    private boolean enabled;
    private int priority;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
