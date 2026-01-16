package net.korperka.antifraud.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.korperka.antifraud.enums.Gender;
import net.korperka.antifraud.enums.MaritalStatus;
import net.korperka.antifraud.enums.Role;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class UserResponseDTO {
    private UUID id;
    private String email;
    private String fullName;
    private String region;
    private Gender gender;
    private Integer age;
    private MaritalStatus maritalStatus;
    private Role role;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
