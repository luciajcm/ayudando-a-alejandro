package org.idea.fithub.security.auth.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.idea.fithub.exceptions.UnauthorizedException;
import org.idea.fithub.exceptions.UserAlreadyExistException;
import org.idea.fithub.global.GlobalExceptionHandler;
import org.idea.fithub.security.auth.domain.AuthService;
import org.idea.fithub.security.auth.dto.*;
import org.idea.fithub.security.auth.jwt.JwtService;
import org.idea.fithub.user.domain.Gender;
import org.idea.fithub.user.domain.Role;
import org.idea.fithub.user.domain.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.idea.fithub.configuration.SecurityConfig;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;

@WebMvcTest(AuthController.class)
@Import({GlobalExceptionHandler.class, SecurityConfig.class})
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtService jwtService;
    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private SignUpRequest validSignUpRequest;
    private SignInRequest validSignInRequest;
    private SignUpResponse signUpResponse;
    private SignInResponse signInResponse;

    @BeforeEach
    void setUp() {
        // Arrange global
        validSignUpRequest = new SignUpRequest();
        validSignUpRequest.setFirstName("Test");
        validSignUpRequest.setLastName("User");
        validSignUpRequest.setUsername("testuser"); // AÑADIDO: Campo username requerido
        validSignUpRequest.setEmail("test@example.com");
        validSignUpRequest.setPassword("Password123!");
        validSignUpRequest.setGender(Gender.MALE);
        validSignUpRequest.setPhoneNumber("+1234567890");
        validSignUpRequest.setRole(Role.LEARNER);
        validSignUpRequest.setBirthday(LocalDate.of(1990, 1, 1));
        validSignUpRequest.setHeight(175.0);
        validSignUpRequest.setWeight(70.0);

        validSignInRequest = new SignInRequest();
        validSignInRequest.setEmail("test@example.com");
        validSignInRequest.setPassword("Password123!");

        signUpResponse = SignUpResponse.builder()
                .message("Congratulations! Your account has been created.")
                .email("test@example.com")
                .accessToken("fake.jwt.token")
                .refreshToken("fake.refresh.token")
                .build();

        signInResponse = SignInResponse.builder()
                .message("Login successful.")
                .accessToken("fake.jwt.token")
                .refreshToken("fake.refresh.token")
                .build();
    }

    @Test
    void shouldReturn201CreatedWhenSignUpIsSuccessful() throws Exception {
        // Arrange
        when(authService.signUp(any(SignUpRequest.class))).thenReturn(signUpResponse);

        // Act & Assert
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validSignUpRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken", is(signUpResponse.getAccessToken())))
                .andExpect(jsonPath("$.email", is(signUpResponse.getEmail())));

        // Verify
        verify(authService).signUp(any(SignUpRequest.class));
    }

    @Test
    void shouldReturn400BadRequestWhenSignUpRequestIsInvalid() throws Exception {
        // Arrange
        validSignUpRequest.setEmail("not-an-email");
        validSignUpRequest.setUsername(null); // Asegurar que username sea null para probar validación

        // Act & Assert
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validSignUpRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("email")));

        // Verify
        verify(authService, never()).signUp(any());
    }

    @Test
    void shouldReturn409ConflictWhenSignUpThrowsUserAlreadyExistsException() throws Exception {
        // Arrange
        String originalErrorMessage = "Email is already registered.";
        when(authService.signUp(any(SignUpRequest.class)))
                .thenThrow(new UserAlreadyExistException(originalErrorMessage));

        // Act & Assert
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validSignUpRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", is(originalErrorMessage)));

        // Verify
        verify(authService).signUp(any(SignUpRequest.class));
    }

    // --- Tests para signIn (POST /auth/signin) ---

    @Test
    void shouldReturn200OkWhenSignInIsSuccessful() throws Exception {
        // Arrange
        when(authService.signIn(any(SignInRequest.class))).thenReturn(signInResponse);

        // Act & Assert
        mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validSignInRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken", is(signInResponse.getAccessToken())));

        // Verify
        verify(authService).signIn(any(SignInRequest.class));
    }

    @Test
    void shouldReturn400BadRequestWhenSignInRequestIsInvalid() throws Exception {
        // Arrange
        validSignInRequest.setPassword("short");

        // Act & Assert
        mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validSignInRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("password")));

        // Verify
        verify(authService, never()).signIn(any());
    }

    @Test
    void shouldReturn401UnauthorizedWhenSignInThrowsUnauthorizedException() throws Exception {
        // Arrange
        String originalErrorMessage = "Invalid email or password.";
        when(authService.signIn(any(SignInRequest.class)))
                .thenThrow(new UnauthorizedException(originalErrorMessage));

        // Act & Assert
        mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validSignInRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message", is(originalErrorMessage)));

        // Verify
        verify(authService).signIn(any(SignInRequest.class));
    }
}