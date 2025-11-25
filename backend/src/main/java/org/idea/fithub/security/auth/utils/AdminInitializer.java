package org.idea.fithub.security.auth.utils;

import lombok.RequiredArgsConstructor;
import org.idea.fithub.user.domain.Gender;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.idea.fithub.user.domain.Role;
import org.idea.fithub.user.domain.User;
import org.idea.fithub.user.domain.UserStatus;
import org.idea.fithub.user.infrastructure.BaseUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminInitializer implements CommandLineRunner {
    private final BaseUserRepository<User> baseUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${fithub.admin.password}")
    private String adminPassword;

    @Value("${fithub.admin.email}")
    private String adminEmail;

    @Override
    public void run(String... args) throws Exception {

        if (baseUserRepository.findByEmail(adminEmail).isPresent()) {
            log.info("La cuenta de administrador ya existe. Omitiendo creación.");
            return;
        }

        log.info("Creando nuevo admin...");

        User adminUser = User.builder()
                .firstName("Admin")
                .lastName("FitHub")
                .username("admin")
                .email(adminEmail)
                .password(passwordEncoder.encode(adminPassword))
                .role(Role.ADMIN)
                .userStatus(UserStatus.AVAILABLE)
                .height(1.0)
                .weight(1.0)
                .gender(Gender.MALE)
                .phoneNumber("000000000")
                .build();

        baseUserRepository.save(adminUser);
        log.info("¡Cuenta de administrador creada exitosamente!");
    }
}
