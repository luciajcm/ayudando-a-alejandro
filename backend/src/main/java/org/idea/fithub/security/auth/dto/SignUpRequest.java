package org.idea.fithub.security.auth.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.idea.fithub.user.domain.Role;
import org.idea.fithub.user.domain.Gender;

import java.time.LocalDate;

@Data
public class SignUpRequest {
    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must contain at least 8 characters")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&]).+$",
            message = "Password must include at least one uppercase letter, one lowercase letter, one number, and one special character"
    )
    private String password;

    @Past(message = "Birthday must be a past date")
    private LocalDate birthday;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^\\+?\\d{9,15}$",
            message = "Phone number must be valid"
    )
    private String phoneNumber;

    @NotNull(message = "Role is required")
    private Role role;

    @NotNull(message = "Height is required")
    private Double height;

    @NotNull(message = "Weight is required")
    private Double weight;

}
