package net.korperka.antifraud.entity;

import jakarta.persistence.*;
import lombok.Data;
import net.korperka.antifraud.enums.Gender;
import net.korperka.antifraud.enums.MaritalStatus;
import net.korperka.antifraud.enums.Role;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 254)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 200)
    private String fullName;

    private Integer age;

    @Column(length = 32)
    private String region;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    private MaritalStatus maritalStatus;

    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean isActive;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}