package net.korperka.antifraud.service;

import lombok.RequiredArgsConstructor;
import net.korperka.antifraud.dto.response.UserListResponse;
import net.korperka.antifraud.dto.response.UserResponseDTO;
import net.korperka.antifraud.entity.User;
import net.korperka.antifraud.mapper.UserMapper;
import net.korperka.antifraud.repository.UserRepository;
import net.korperka.antifraud.utils.JWTUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    public UserListResponse getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
        Page<User> userPage = userRepository.findAll(pageable);

        List<UserResponseDTO> users = userPage.getContent().stream().map(userMapper::toDto).toList();

        return new UserListResponse(users, users.size(), page, getAllUsers().size());
    }
}
