package net.korperka.antifraud.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import net.korperka.antifraud.dto.request.UserCreateRequest;
import net.korperka.antifraud.dto.request.UserUpdateRequest;
import net.korperka.antifraud.dto.response.UserListResponse;
import net.korperka.antifraud.dto.response.UserResponseDTO;
import net.korperka.antifraud.exception.NotFoundException;
import net.korperka.antifraud.service.UserService;
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

    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDTO> updateCurrentUser(@Valid @RequestBody UserUpdateRequest request, Principal principal) {
        String userIdString = principal.getName();
        UUID userId = UUID.fromString(userIdString);

        return ResponseEntity.ok(userService.updateUser(userId, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> deactivateUser(@PathVariable UUID id) {
        userService.deactivateUser(id);
        return ResponseEntity.status(204).build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable UUID id, @Valid @RequestBody UserUpdateRequest request, Principal principal) {
        String userIdString = principal.getName();
        UUID userId = UUID.fromString(userIdString);

        return ResponseEntity.ok(userService.updateUser(userId, id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable UUID id, Principal principal) {
        String userIdString = principal.getName();
        UUID userId = UUID.fromString(userIdString);

        return ResponseEntity.ok(userService.getUserById(userId, id));
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDTO> getCurrentUser(Principal principal) {
        String userIdString = principal.getName();
        UUID userId = UUID.fromString(userIdString);
        UserResponseDTO user = userService.getUserById(userId);

        if(user == null) throw new NotFoundException();

        return ResponseEntity.ok(user);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserListResponse> getUsers(@RequestParam(defaultValue = "0") @Min(0) int page,
                                                     @RequestParam(defaultValue = "20") @Min(1) int size) {
        return ResponseEntity.ok(userService.getAllUsers(page, size));
    }
}
