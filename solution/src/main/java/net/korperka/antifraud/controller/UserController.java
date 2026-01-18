package net.korperka.antifraud.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import net.korperka.antifraud.dto.response.UserListResponse;
import net.korperka.antifraud.dto.response.UserResponseDTO;
import net.korperka.antifraud.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "Управление пользователями и профилями")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDTO> getMe(Principal principal) {
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
