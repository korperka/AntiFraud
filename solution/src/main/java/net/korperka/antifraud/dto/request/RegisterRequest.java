package net.korperka.antifraud.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import net.korperka.antifraud.enums.Gender;
import net.korperka.antifraud.enums.MaritalStatus;

@Data
public class RegisterRequest {
    @NotBlank @Email
    @Size(max = 254)
    private String email;

    @NotBlank
    @Size(min = 8, max = 72)
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$")
    private String password;

    @NotBlank
    @Size(min = 2, max = 200)
    private String fullName;

    @Min(18) @Max(120)
    private Integer age;

    @Size(max = 32)
    private String region;

    private Gender gender;
    private MaritalStatus maritalStatus;
}
