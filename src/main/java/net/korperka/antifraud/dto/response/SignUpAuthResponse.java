package net.korperka.antifraud.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignUpAuthResponse {
    private String accessToken;
    private UserResponse user;
}
