package org.idea.fithub.user.infrastructure;

import org.idea.fithub.BaseRepositoryTest; // 1. Tu clase base
import org.idea.fithub.user.domain.Gender;
import org.idea.fithub.user.domain.Role;
import org.idea.fithub.user.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas de integración para UserRepository.
 * - Usa @DataJpaTest (heredado de BaseRepositoryTest).
 * - Usa TestContainers (heredado de BaseRepositoryTest).
 * - Prueba solo consultas personalizadas (findByEmail, existsByUsername).
 */
class UserRepositoryTest extends BaseRepositoryTest { // 2. Extiende la clase base

    @Autowired
    private TestEntityManager entityManager; // Para preparar los datos

    @Autowired
    private BaseUserRepository baseUserRepository; // El repositorio a probar

    /**
     * Prueba la consulta personalizada: findByEmail
     * Caso: Happy Path (El usuario existe)
     */
    @Test
    void shouldFindUserByEmailWhenEmailExists() {
        /// Arrange
        User user = User.builder()
                .email("findme@test.com")
                .username("find-user")
                .password("password123")
                .firstName("Test")
                .lastName("Find")
                .phoneNumber("111111111")
                .gender(Gender.FEMALE)
                .role(Role.LEARNER)
                .height(170.0)  // Añadido: campo obligatorio
                .weight(65.0)   // Añadido: campo obligatorio
                .build();

        entityManager.persistAndFlush(user);

        /// Act
        Optional<User> foundUser = baseUserRepository.findByEmail("findme@test.com");

        /// Assert
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("findme@test.com");
    }

    /**
     * Prueba la consulta personalizada: findByEmail
     * Caso: Edge Case (El email no existe)
     */
    @Test
    void shouldReturnEmptyOptionalWhenEmailDoesNotExist() {
        /// Arrange
        // No persistimos ningún usuario con este email

        /// Act
        Optional<User> foundUser = baseUserRepository.findByEmail("noexiste@test.com");

        /// Assert
        assertThat(foundUser).isNotPresent();
    }

    /**
     * Prueba la consulta personalizada: existsByUsername
     * Caso: Happy Path (El username existe)
     */
    @Test
    void shouldReturnTrueWhenUsernameExists() {
        /// Arrange
        User user = User.builder()
                .email("exists@test.com")
                .username("exists-user")
                .password("password123")
                .firstName("Test")
                .lastName("Exists")
                .phoneNumber("222222222")
                .gender(Gender.MALE)
                .role(Role.TRAINER)
                .height(180.0)  // Añadido: campo obligatorio
                .weight(75.0)   // Añadido: campo obligatorio
                .build();

        entityManager.persistAndFlush(user);

        /// Act
        boolean exists = baseUserRepository.existsByUsername("exists-user");

        /// Assert
        assertThat(exists).isTrue();
    }

    /**
     * Prueba la consulta personalizada: existsByUsername
     * Caso: Edge Case (El username no existe)
     */
    @Test
    void shouldReturnFalseWhenUsernameDoesNotExist() {
        /// Arrange
        // No persistimos ningún usuario con este username

        /// Act
        boolean exists = baseUserRepository.existsByUsername("ghost-user");

        /// Assert
        assertThat(exists).isFalse();
    }
}