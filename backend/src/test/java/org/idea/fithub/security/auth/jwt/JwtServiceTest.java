package org.idea.fithub.security.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.idea.fithub.user.domain.Role;
import org.idea.fithub.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;

    // --- Configuración de prueba ---
    private final String testSecret = "miSecretoSuperSeguroDebeTenerAlMenos32BytesParaHS256";
    private final Long testAccessTokenExpiration = TimeUnit.MINUTES.toMillis(15); // 15 minutos en ms
    private final Long testRefreshTokenExpiration = TimeUnit.DAYS.toMillis(7);    // 7 días en ms
    private final Long shortExpiration = 1L; // 1 milisegundo para probar expiración

    private User testUser;
    private String testUserEmail = "test@user.com";

    @BeforeEach
    void setUp() {
        /// Arrange global
        jwtService = new JwtService(); // Instancia real

        ReflectionTestUtils.setField(jwtService, "secret", testSecret);
        ReflectionTestUtils.setField(jwtService, "accessTokenExpiration", testAccessTokenExpiration);
        ReflectionTestUtils.setField(jwtService, "refreshTokenExpiration", testRefreshTokenExpiration);

        testUser = User.builder()
                .id(1L)
                .email(testUserEmail)
                .username("testuser")
                .password("password")
                .firstName("Test")
                .lastName("User")
                .gender(Role.LEARNER == Role.LEARNER ? org.idea.fithub.user.domain.Gender.MALE : org.idea.fithub.user.domain.Gender.MALE) // Usar el Gender apropiado
                .phoneNumber("+123456789")
                .role(Role.LEARNER)
                .birthday(LocalDate.of(1990, 1, 1))
                .height(175.0)
                .weight(70.0)
                .userStatus(org.idea.fithub.user.domain.UserStatus.AVAILABLE)
                .build();
    }

    private SecretKey getTestSigningKey() {
        return Keys.hmacShaKeyFor(testSecret.getBytes());
    }

    // --- Tests para generateToken ---

    @Test
    void shouldGenerateTokenSuccessfully() {
        String token = jwtService.generateToken(testUser);

        Claims claims = Jwts.parser()
                .verifyWith(getTestSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertThat(claims.getSubject()).isEqualTo(testUserEmail);
        assertThat(claims.get("roles", List.class)).containsExactly(Role.LEARNER.name());
        assertThat(claims.get("userId")).isEqualTo(1);
        assertThat(claims.getIssuedAt()).isBeforeOrEqualTo(new Date());
        assertThat(claims.getExpiration()).isAfter(new Date());
    }

    // --- Tests para extractUsername ---

    @Test
    void shouldExtractUsernameFromValidToken() {
        /// Arrange
        String token = jwtService.generateToken(testUser);

        /// Act
        String extractedUsername = jwtService.extractUsername(token);

        /// Assert
        assertThat(extractedUsername).isEqualTo(testUserEmail);
    }

    // --- Tests para isTokenValid ---

    @Test
    void shouldReturnTrueWhenTokenIsValid() {
        /// Arrange
        String token = jwtService.generateToken(testUser);

        /// Act
        boolean isValid = jwtService.isTokenValid(token);

        /// Assert
        assertThat(isValid).isTrue();
    }

    @Test
    void shouldReturnFalseWhenTokenSignatureIsInvalid() {
        /// Arrange
        String token = jwtService.generateToken(testUser);
        String tamperedToken = token.substring(0, token.length() - 1) + "X";

        /// Act
        boolean isValid = jwtService.isTokenValid(tamperedToken);

        /// Assert
        assertThat(isValid).isFalse();
    }

    @Test
    void shouldReturnFalseWhenTokenIsExpired() throws InterruptedException {
        /// Arrange
        JwtService shortExpiryJwtService = new JwtService();
        ReflectionTestUtils.setField(shortExpiryJwtService, "secret", testSecret);
        ReflectionTestUtils.setField(shortExpiryJwtService, "accessTokenExpiration", shortExpiration); // 1 ms

        String expiredToken = shortExpiryJwtService.generateToken(testUser);

        Thread.sleep(2); // 2 ms para asegurar que expire

        /// Act
        boolean isValid = jwtService.isTokenValid(expiredToken); // Validamos con el servicio original

        /// Assert
        assertThat(isValid).isFalse();
    }

    @Test
    void shouldReturnFalseWhenTokenIsNull() {
        /// Act
        boolean isValid = jwtService.isTokenValid(null);
        /// Assert
        assertThat(isValid).isFalse();
    }

    @Test
    void shouldReturnFalseWhenTokenIsEmpty() {
        /// Act
        boolean isValid = jwtService.isTokenValid("");
        /// Assert
        assertThat(isValid).isFalse();
    }

    @Test
    void shouldReturnFalseWhenTokenIsMalformed() {
        /// Arrange
        String malformedToken = "this.is.not.a.jwt";
        /// Act
        boolean isValid = jwtService.isTokenValid(malformedToken);
        /// Assert
        assertThat(isValid).isFalse();
    }

    // --- Tests para generateRefreshToken ---

    @Test
    void shouldGenerateRefreshTokenSuccessfully() {
        /// Act
        String refreshToken = jwtService.generateRefreshToken(testUser);

        /// Assert
        assertThat(refreshToken).isNotNull().isNotEmpty();
        // Verifica que puede ser parseado y tiene el subject correcto
        Claims claims = Jwts.parser()
                .verifyWith(getTestSigningKey())
                .build()
                .parseSignedClaims(refreshToken)
                .getPayload();

        assertThat(claims.getSubject()).isEqualTo(testUserEmail);
        assertThat(claims.getExpiration()).isAfter(new Date());
        // El refresh token NO debería tener roles
        assertThat(claims.get("roles")).isNull();
        // El refresh token NO debería tener userId
        assertThat(claims.get("userId")).isNull();
    }

    // --- Tests adicionales para verificar diferencias entre access y refresh tokens ---

    @Test
    void shouldGenerateDifferentTokensForAccessAndRefresh() {
        /// Act
        String accessToken = jwtService.generateToken(testUser);
        String refreshToken = jwtService.generateRefreshToken(testUser);

        /// Assert
        assertThat(accessToken).isNotEqualTo(refreshToken);

        Claims accessClaims = Jwts.parser()
                .verifyWith(getTestSigningKey())
                .build()
                .parseSignedClaims(accessToken)
                .getPayload();

        Claims refreshClaims = Jwts.parser()
                .verifyWith(getTestSigningKey())
                .build()
                .parseSignedClaims(refreshToken)
                .getPayload();

        // Access token tiene claims adicionales
        assertThat(accessClaims.get("roles")).isNotNull();
        assertThat(accessClaims.get("userId")).isNotNull();

        // Refresh token no tiene claims adicionales
        assertThat(refreshClaims.get("roles")).isNull();
        assertThat(refreshClaims.get("userId")).isNull();

        // Ambos tienen el mismo subject
        assertThat(accessClaims.getSubject()).isEqualTo(refreshClaims.getSubject());
    }

    @Test
    void shouldHandleDifferentUserRolesCorrectly() {
        /// Arrange
        User adminUser = User.builder()
                .id(2L)
                .email("admin@test.com")
                .username("adminuser")
                .password("password")
                .firstName("Admin")
                .lastName("User")
                .gender(org.idea.fithub.user.domain.Gender.MALE)
                .phoneNumber("+987654321")
                .role(Role.ADMIN)
                .birthday(LocalDate.of(1985, 1, 1))
                .height(180.0)
                .weight(80.0)
                .userStatus(org.idea.fithub.user.domain.UserStatus.AVAILABLE)
                .build();

        /// Act
        String token = jwtService.generateToken(adminUser);

        /// Assert
        Claims claims = Jwts.parser()
                .verifyWith(getTestSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertThat(claims.get("roles", List.class)).containsExactly(Role.ADMIN.name());

        assertThat(claims.get("userId")).isEqualTo(2);
    }
}