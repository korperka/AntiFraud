package net.korperka.antifraud.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import net.korperka.antifraud.dto.request.UserCreateRequest;
import net.korperka.antifraud.dto.request.UserUpdateRequest;
import net.korperka.antifraud.dto.response.UserListResponse;
import net.korperka.antifraud.dto.response.UserResponseDTO;
import net.korperka.antifraud.service.AuthService;
import net.korperka.antifraud.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "Управление пользователями и профилями")
public class UserController {
    private final UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserCreateRequest request) {
        return ResponseEntity.status(201).body(userService.createUser(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@RequestParam UUID id, Principal principal) {
        String userIdString = principal.getName();
        UUID userId = UUID.fromString(userIdString);

        return ResponseEntity.ok(userService.getUserById(userId, id));
    }

    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDTO> updateCurrentUser(@Valid @RequestBody UserUpdateRequest request, Principal principal) {
        String userIdString = principal.getName();
        UUID userId = UUID.fromString(userIdString);

        return ResponseEntity.ok(userService.updateUser(userId, request));
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDTO> getCurrentUser(Principal principal) {
        String userIdString = principal.getName();
        UUID userId = UUID.fromString(userIdString);

        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserListResponse> getUsers(@RequestParam(defaultValue = "0") @Min(0) int page,
                                                     @RequestParam(defaultValue = "20") @Min(1) int size) {
        return ResponseEntity.ok(userService.getAllUsers(page, size));
    }
}
