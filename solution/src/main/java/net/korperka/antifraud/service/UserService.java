package net.korperka.antifraud.service;

import lombok.RequiredArgsConstructor;
import net.korperka.antifraud.dto.request.UserCreateRequest;
import net.korperka.antifraud.dto.request.UserUpdateRequest;
import net.korperka.antifraud.dto.response.UserListResponse;
import net.korperka.antifraud.dto.response.UserResponseDTO;
import net.korperka.antifraud.entity.User;
import net.korperka.antifraud.enums.Role;
import net.korperka.antifraud.exception.InvalidCredentialsException;
import net.korperka.antifraud.exception.UserAlreadyExistsException;
import net.korperka.antifraud.mapper.UserMapper;
import net.korperka.antifraud.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserResponseDTO getUserById(UUID id) {
        User user = userRepository.findById(id).orElseThrow(InvalidCredentialsException::new);

        return userMapper.toDto(user);
    }

    public UserResponseDTO updateUser(UUID id, UserUpdateRequest request) {
        User user = userRepository.findById(id).orElseThrow(InvalidCredentialsException::new);

        user.setFullName(request.getFullName());
        user.setAge(request.getAge());
        user.setRegion(request.getRegion());
        user.setGender(request.getGender());
        user.setMaritalStatus(request.getMaritalStatus());
        user.setUpdatedAt(LocalDateTime.now());

        if(request.getRole() != null || request.getActive() != null) {
            if(user.getRole() != Role.ADMIN) throw new AccessDeniedException("Недостаточно прав для выполнения операции");

            user.setRole(request.getRole());
            user.setActive(request.getActive());
        }

        User savedEntity = userRepository.save(user);

        return userMapper.toDto(savedEntity);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public UserResponseDTO createUser(UserCreateRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException(request.getEmail());
        }

        User user = userMapper.toEntity(request);

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        User savedEntity = userRepository.save(user);

        return userMapper.toDto(savedEntity);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public UserListResponse getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
        Page<User> userPage = userRepository.findAll(pageable);

        List<UserResponseDTO> users = userPage.getContent().stream().map(userMapper::toDto).toList();

        return new UserListResponse(users, (int) userPage.getTotalElements(), userPage.getNumber(), userPage.getSize());
    }
}
