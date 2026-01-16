package net.korperka.antifraud.service;

import lombok.RequiredArgsConstructor;
import net.korperka.antifraud.dto.request.UserDTO;
import net.korperka.antifraud.dto.response.AuthResponse;
import net.korperka.antifraud.entity.User;
import net.korperka.antifraud.enums.Role;
import net.korperka.antifraud.exception.UserAlreadyExistsException;
import net.korperka.antifraud.mapper.UserMapper;
import net.korperka.antifraud.repository.UserRepository;
import net.korperka.antifraud.utils.JWTUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JWTUtils jwtUtils;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public AuthResponse register(UserDTO request) {
        if(userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException(request.getEmail());
        }

        User user = userMapper.toEntity(request);

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        User savedEntity = userRepository.save(user);

        return new AuthResponse(jwtUtils.generateToken(savedEntity), userMapper.toDto(savedEntity));
    }
}
