package org.idea.fithub.security.auth.domain;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.idea.fithub.event.UserRegisterEvent;
import org.idea.fithub.exceptions.ResourceNotFoundException;
import org.idea.fithub.exceptions.UnauthorizedException;
import org.idea.fithub.exceptions.UserAlreadyExistException;
import org.idea.fithub.learner.domain.Learner;
import org.idea.fithub.learner.infrastructure.LearnerRepository;
import org.idea.fithub.security.auth.dto.*;
import org.idea.fithub.security.auth.jwt.JwtService;
import org.idea.fithub.trainer.domain.Trainer;
import org.idea.fithub.trainer.infrastructure.TrainerRepository;
import org.idea.fithub.user.domain.Gender;
import org.idea.fithub.user.domain.Role;
import org.idea.fithub.user.domain.User;
import org.idea.fithub.user.domain.UserStatus;
import org.idea.fithub.user.infrastructure.BaseUserRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.idea.fithub.email.service.EmailService;
import java.time.LocalDateTime;
import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TrainerRepository trainerRepository;
    private final LearnerRepository learnerRepository;
    private final BaseUserRepository<User> baseUserRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    @Value("${google.client-id}")
    private String googleClientId;


    @Transactional
    public SignUpResponse signUp(SignUpRequest request) {
        if (baseUserRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new UserAlreadyExistException("Email is already registered.");
        }
        if (baseUserRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistException("Username is already taken.");
        }

        User savedUser;

        switch (request.getRole()) {
            case TRAINER -> {
                Trainer trainer = Trainer.builder()
                        .firstName(request.getFirstName())
                        .lastName(request.getLastName())
                        .username(request.getUsername())
                        .email(request.getEmail())
                        .password(passwordEncoder.encode(request.getPassword()))
                        .birthday(request.getBirthday())
                        .gender(request.getGender())
                        .phoneNumber(request.getPhoneNumber())
                        .role(request.getRole())
                        .weight(request.getWeight())
                        .height(request.getHeight())
                        .userStatus(UserStatus.AVAILABLE)
                        .build();
                savedUser = trainerRepository.save(trainer);
            }
            case LEARNER -> {
                Learner learner = Learner.builder()
                        .firstName(request.getFirstName())
                        .lastName(request.getLastName())
                        .username(request.getUsername())
                        .email(request.getEmail())
                        .password(passwordEncoder.encode(request.getPassword()))
                        .birthday(request.getBirthday())
                        .gender(request.getGender())
                        .phoneNumber(request.getPhoneNumber())
                        .role(request.getRole())
                        .weight(request.getWeight())
                        .height(request.getHeight())
                        .userStatus(UserStatus.AVAILABLE)
                        .build();
                savedUser = learnerRepository.save(learner);
            }
            default -> throw new IllegalArgumentException("Unrecognized role: " + request.getRole());
        }

        applicationEventPublisher.publishEvent(
                new UserRegisterEvent(this, savedUser));

        String token = jwtService.generateToken(savedUser);
        String refreshToken = jwtService.generateRefreshToken(savedUser);

        return SignUpResponse.builder()
                .message("Congratulations! Your account has been created.")
                .accessToken(token)
                .refreshToken(refreshToken)
                .email(savedUser.getEmail())
                .build();
    }

    @Transactional(readOnly = true)
    public SignInResponse signIn(SignInRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
            var account = (User) authentication.getPrincipal();

            var token = jwtService.generateToken(account);
            var refreshToken = jwtService.generateRefreshToken(account);

            return SignInResponse.builder()
                    .message("Login successful.")
                    .accessToken(token)
                    .refreshToken(refreshToken)
                    .build();

        } catch (BadCredentialsException | UsernameNotFoundException e) {
            throw new UnauthorizedException("Email o contraseña incorrectos");
        }
    }

    @Transactional(readOnly = true)
    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        if (!jwtService.isTokenValid(refreshToken)) {
            throw new UnauthorizedException("Invalid or expired refresh token.");
        }

        String username = jwtService.extractUsername(refreshToken);
        UserDetails userDetails = baseUserRepository.findByEmail(username).orElseThrow(
                () -> new ResourceNotFoundException("User not found with email: " + username));

        String newAccessToken = jwtService.generateToken(userDetails);
        String newRefreshToken = jwtService.generateRefreshToken(userDetails);

        return RefreshTokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    @Transactional
    public SignInResponse signInWithGoogle(String googleToken){
        try{
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(googleToken);
            if (idToken == null) {
                throw new UnauthorizedException("Token de Google inválido");
            }

            // 2. Obtener datos del usuario de Google
            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            String firstName = (String) payload.get("given_name");
            String lastName = (String) payload.get("family_name");

            // --- 🛠️ LA CORRECCIÓN VA AQUÍ ---

            // Si Google no nos da un apellido, ponemos un "." como placeholder.
            // La base de datos no permite que sea nulo.
            if (lastName == null || lastName.isBlank()) {
                lastName = ".";
            }
            // También es bueno asegurar el nombre
            if (firstName == null || firstName.isBlank()) {
                // Extrae el nombre del email (ej. "carlos" de "carlos@gmail.com")
                firstName = email.split("@")[0];
            }
            // ------------------------------------


            // 3. Lógica de "Buscar o Crear" (Find or Create)
            String finalFirstName = firstName;
            String finalLastName = lastName;
            User user = baseUserRepository.findByEmail(email)
                    .orElseGet(() -> {
                        // Si el usuario no existe, lo "registramos"
                        log.info("Creando nuevo usuario desde Google: {}", email);
                        // ¡Usa los valores por defecto que definiste en AdminInitializer!
                        User newUser = User.builder()
                                .email(email)
                                .firstName(finalFirstName)
                                .lastName(finalLastName)
                                .username(email) // O genera uno
                                .password(passwordEncoder.encode(java.util.UUID.randomUUID().toString())) // Contraseña aleatoria, no la usará
                                .role(Role.LEARNER) // Rol por defecto
                                .userStatus(UserStatus.AVAILABLE)
                                .height(1.0)
                                .weight(1.0)
                                .gender(Gender.MALE)
                                .phoneNumber(java.util.UUID.randomUUID().toString().substring(0, 9)) // Teléfono aleatorio
                                .build();
                        return baseUserRepository.save(newUser);
                    });

            // 4. Generar TUS propios tokens
            String accessToken = jwtService.generateToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            return SignInResponse.builder()
                    .message("Login with Google successful.")
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();

        } catch (Exception e) {
            log.error("Error en la autenticación con Google: {}", e.getMessage());
            throw new UnauthorizedException("Error al validar el token de Google");
        }
    }
    // Añade estas inyecciones al constructor:
// private final PasswordResetTokenRepository tokenRepository;

    @Transactional
    public void requestPasswordReset(String email) {
        // 1. Buscar usuario
        User user = baseUserRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        // 2. Generar Token único
        String token = java.util.UUID.randomUUID().toString();

        // 3. Guardar Token en BD (Expira en 15 min)
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(15))
                .build();

        tokenRepository.save(resetToken);

        // 4. Crear Link (Apunta a tu Frontend React)
        String link = "http://localhost:5173/reset-password?token=" + token;

        // 5. Enviar Email
        // Asegúrate de crear este método en EmailService como vimos arriba
        emailService.sendPasswordResetEmail(user.getEmail(), user.getFirstName(), link);
    }
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        // 1. Buscar el token
        PasswordResetToken resetToken = tokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new ResourceNotFoundException("Token inválido o no encontrado"));

        // 2. Verificar si expiró
        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new UnauthorizedException("El enlace ha expirado. Solicita uno nuevo.");
        }

        // 3. Actualizar contraseña del usuario
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        baseUserRepository.save(user);

        // 4. Borrar el token para que no se use dos veces
        tokenRepository.delete(resetToken);
    }
}