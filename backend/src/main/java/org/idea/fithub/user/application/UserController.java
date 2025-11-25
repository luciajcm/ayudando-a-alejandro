package org.idea.fithub.user.application;

import lombok.RequiredArgsConstructor;
import org.idea.fithub.user.domain.User;
import org.idea.fithub.user.infrastructure.BaseUserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final BaseUserRepository<User> userRepository;
    // 1. ENDPOINT "QUIEN SOY" (Para el ProfileGuard)
    @GetMapping("/me")
    public ResponseEntity<User> me() {
        // Spring Security ya validó el token antes de llegar aquí
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        // Obtenemos el usuario actual directamente del contexto de seguridad
        // Ojo: auth.getPrincipal() devuelve el objeto User que guardó el JwtFilter
        User currentUser = (User) auth.getPrincipal();

        return ResponseEntity.ok(currentUser);
    }

    // 2. ENDPOINT ACTUALIZAR (Para el Formulario de Onboarding)
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        return userRepository.findById(id)
                .map(user -> {
                    // Actualizamos solo los campos que el Onboarding envía
                    if (userDetails.getFirstName() != null) user.setFirstName(userDetails.getFirstName());
                    if (userDetails.getUsername() != null) user.setUsername(userDetails.getUsername());
                    if (userDetails.getLastName() != null) user.setLastName(userDetails.getLastName());
                    if (userDetails.getHeight() != null) user.setHeight(userDetails.getHeight());
                    if (userDetails.getWeight() != null) user.setWeight(userDetails.getWeight());
                    if (userDetails.getGender() != null) user.setGender(userDetails.getGender());
                    if (userDetails.getBirthday() != null) user.setBirthday(userDetails.getBirthday());
                    if (userDetails.getPhoneNumber() != null) user.setPhoneNumber(userDetails.getPhoneNumber());

                    // Guardamos en la BD
                    return ResponseEntity.ok(userRepository.save(user));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
