package net.korperka.antifraud.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("isActive")
    private boolean active;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime updatedAt;
}
