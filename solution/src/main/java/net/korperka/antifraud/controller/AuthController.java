package net.korperka.antifraud.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.korperka.antifraud.dto.request.LoginRequest;
import net.korperka.antifraud.dto.request.RegisterRequest;
import net.korperka.antifraud.dto.response.SignInAuthResponse;
import net.korperka.antifraud.dto.response.SignUpAuthResponse;
import net.korperka.antifraud.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
@Tag(name = "Auth", description = "Регистрация и авторизация")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<SignUpAuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.status(201).body(authService.register(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<SignInAuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }
}
