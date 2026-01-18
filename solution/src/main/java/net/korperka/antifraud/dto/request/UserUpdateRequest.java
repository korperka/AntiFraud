package net.korperka.antifraud.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import net.korperka.antifraud.enums.Gender;
import net.korperka.antifraud.enums.MaritalStatus;
import net.korperka.antifraud.enums.Role;

@Getter
public class UserUpdateRequest {
    @NotBlank
    @Size(min = 2, max = 200)
    private String fullName;

    @Min(18) @Max(120)
    private Integer age;

    @Size(max = 32)
    private String region;

    private Gender gender;
    private MaritalStatus maritalStatus;

    private Role role;
    @JsonProperty("isActive")
    private Boolean active;
}
