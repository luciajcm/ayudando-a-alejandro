package org.idea.fithub.user.domain;

import org.idea.fithub.user.infrastructure.BaseUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias para UserService (UserDetailsService).
 * - Usa Mockito para aislar el servicio.
 * - Mockea UserRepository.
 * - Prueba el caso de éxito y el lanzamiento de UsernameNotFoundException.
 */
@ExtendWith(MockitoExtension.class) // Habilita Mockito
class UserServiceTest {

    @Mock
    private BaseUserRepository baseUserRepository; // La dependencia a mockear

    @InjectMocks
    private UserService userService; // La clase real bajo prueba

    private User testUser;
    private String userEmail = "test@example.com";

    @BeforeEach
    void setUp() {
        /// Arrange global
        testUser = User.builder()
                .id(1L)
                .email(userEmail)
                .username("testuser")
                .password("encodedPassword") // No importa el valor real aquí
                .role(Role.LEARNER)
                .build();
    }

    /**
     * Prueba el método: loadUserByUsername
     * Caso: Happy Path (El usuario existe en el repositorio)
     */
    @Test
    void shouldLoadUserByUsernameWhenEmailExists() {
        /// Arrange
        // Configuramos el mock para que devuelva nuestro usuario de prueba
        // cuando se llame a findByEmail con el email correcto.
        when(baseUserRepository.findByEmail(userEmail)).thenReturn(Optional.of(testUser));

        /// Act
        // Llamamos al método bajo prueba. Esperamos que devuelva el UserDetails.
        UserDetails userDetails = userService.loadUserByUsername(userEmail);

        /// Assert
        assertThat(userDetails).isNotNull();
        // Verificamos que el UserDetails devuelto es nuestro usuario de prueba.
        // Como User implementa UserDetails, podemos comparar directamente.
        assertThat(userDetails).isEqualTo(testUser);

        /// Verify (Opcional pero bueno)
        // Verificamos que el método findByEmail fue llamado exactamente una vez.
        verify(baseUserRepository).findByEmail(userEmail);
    }

    /**
     * Prueba el método: loadUserByUsername
     * Caso: Negativo (El usuario NO existe en el repositorio)
     * Prueba el .orElseThrow() que corregimos.
     */
    @Test
    void shouldThrowUsernameNotFoundExceptionWhenEmailDoesNotExist() {
        /// Arrange
        String nonExistentEmail = "notfound@example.com";
        // Configuramos el mock para que devuelva un Optional vacío,
        // simulando que el usuario no fue encontrado.
        when(baseUserRepository.findByEmail(nonExistentEmail)).thenReturn(Optional.empty());

        /// Act & Assert
        // Usamos assertThatThrownBy para verificar que se lanza la excepción correcta.
        assertThatThrownBy(() -> userService.loadUserByUsername(nonExistentEmail))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found with email: " + nonExistentEmail);

        /// Verify (Opcional)
        // Verificamos que findByEmail fue llamado.
        verify(baseUserRepository).findByEmail(nonExistentEmail);
    }
}