package net.korperka.antifraud.service;

import lombok.RequiredArgsConstructor;
import net.korperka.antifraud.entity.User;
import net.korperka.antifraud.mapper.UserMapper;
import net.korperka.antifraud.repository.UserRepository;
import net.korperka.antifraud.utils.JWTUtils;
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
}
