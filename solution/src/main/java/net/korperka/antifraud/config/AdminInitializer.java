package net.korperka.antifraud.config;

import lombok.RequiredArgsConstructor;
import net.korperka.antifraud.dto.request.UserCreateRequest;
import net.korperka.antifraud.enums.Role;
import net.korperka.antifraud.repository.UserRepository;
import net.korperka.antifraud.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {
    private final UserService userService;
    private final UserRepository userRepository;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.fullname}")
    private String adminFullname;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) throws Exception {
        if(userRepository.existsByEmail(adminEmail)) {
            return;
        }

        UserCreateRequest request = new UserCreateRequest();
        request.setEmail(adminEmail);
        request.setPassword(adminPassword);
        request.setFullName(adminFullname);
        request.setRole(Role.ADMIN);

        userService.createUser(request);

        System.out.println("ADMIN CREATED");
    }
}
