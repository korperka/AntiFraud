package net.korperka.antifraud.service;

import lombok.RequiredArgsConstructor;
import net.korperka.antifraud.dto.request.UserCreateRequest;
import net.korperka.antifraud.dto.request.UserUpdateRequest;
import net.korperka.antifraud.dto.response.UserListResponse;
import net.korperka.antifraud.dto.response.UserResponseDTO;
import net.korperka.antifraud.entity.User;
import net.korperka.antifraud.enums.Role;
import net.korperka.antifraud.exception.InvalidCredentialsException;
import net.korperka.antifraud.exception.NotFoundException;
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

    public void deactivateUser(UUID id) {
        User user = userRepository.findById(id).orElseThrow(NotFoundException::new);
        user.setActive(false);

        userRepository.save(user);
    }

    public UserResponseDTO getUserById(UUID sourceId, UUID targetId) {
        User source = userRepository.findById(sourceId).orElseThrow(NotFoundException::new);
        User target = userRepository.findById(targetId).orElseThrow(NotFoundException::new);

        if(source.getRole() != Role.ADMIN && !sourceId.equals(targetId)) throw new AccessDeniedException("Недостаточно прав для выполнения операции");

        return userMapper.toDto(target);
    }

    public UserResponseDTO updateUser(UUID sourceId, UUID targetId, UserUpdateRequest request) {
        User target = userRepository.findById(targetId).orElseThrow(NotFoundException::new);
        User source = userRepository.findById(sourceId).orElseThrow(NotFoundException::new);

        if(source.getRole() != Role.ADMIN && !sourceId.equals(targetId)) throw new AccessDeniedException("Недостаточно прав для выполнения операции");

        target.setFullName(request.getFullName());
        target.setAge(request.getAge());
        target.setRegion(request.getRegion());
        target.setGender(request.getGender());
        target.setMaritalStatus(request.getMaritalStatus());
        target.setUpdatedAt(LocalDateTime.now());

        if(request.getRole() != null || request.getActive() != null) {
            if(source.getRole() != Role.ADMIN) throw new AccessDeniedException("Недостаточно прав для выполнения операции");

            target.setRole(request.getRole());
            target.setActive(request.getActive());
        }

        return userMapper.toDto(userRepository.save(target));
    }

    public UserResponseDTO updateUser(UUID id, UserUpdateRequest request) {
        return updateUser(id, id, request);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public UserResponseDTO createUser(UserCreateRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) throw new UserAlreadyExistsException(request.getEmail());

        User user = userMapper.toEntity(request);

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        return userMapper.toDto(userRepository.save(user));
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
