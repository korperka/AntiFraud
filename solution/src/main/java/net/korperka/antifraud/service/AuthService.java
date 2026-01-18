package net.korperka.antifraud.service;

import lombok.RequiredArgsConstructor;
import net.korperka.antifraud.dto.request.LoginRequest;
import net.korperka.antifraud.dto.request.RegisterRequest;
import net.korperka.antifraud.dto.request.UserCreateRequest;
import net.korperka.antifraud.dto.response.SignInAuthResponse;
import net.korperka.antifraud.dto.response.SignUpAuthResponse;
import net.korperka.antifraud.dto.response.UserResponseDTO;
import net.korperka.antifraud.entity.User;
import net.korperka.antifraud.enums.Role;
import net.korperka.antifraud.exception.InvalidCredentialsException;
import net.korperka.antifraud.exception.UserAlreadyExistsException;
import net.korperka.antifraud.exception.UserDeactivatedException;
import net.korperka.antifraud.mapper.UserMapper;
import net.korperka.antifraud.repository.UserRepository;
import net.korperka.antifraud.utils.JWTUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JWTUtils jwtUtils;

    public SignInAuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(InvalidCredentialsException::new);

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) throw new InvalidCredentialsException();
        if(!user.isActive()) throw new UserDeactivatedException();

        String token = jwtUtils.generateToken(user);
        UserResponseDTO userDto = userMapper.toDto(user);

        return new SignInAuthResponse(token, userDto, 3600L);
    }

    public SignUpAuthResponse register(RegisterRequest request) {
        if(userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException(request.getEmail());
        }

        User user = userMapper.toEntity(request);

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(userRepository.count() == 0 ? Role.ADMIN : Role.USER);
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        User savedEntity = userRepository.save(user);

        return new SignUpAuthResponse(jwtUtils.generateToken(savedEntity), userMapper.toDto(savedEntity));
    }
}
