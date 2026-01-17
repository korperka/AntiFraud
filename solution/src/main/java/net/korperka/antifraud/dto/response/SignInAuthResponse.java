package net.korperka.antifraud.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignInAuthResponse {
    private String accessToken;
    private UserResponseDTO user;
    private Long expiresIn;
}
