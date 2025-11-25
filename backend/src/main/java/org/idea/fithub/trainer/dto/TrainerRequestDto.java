package org.idea.fithub.trainer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.idea.fithub.user.domain.Gender;
import org.idea.fithub.user.domain.Role;

import java.time.LocalDate;

@Data
public class TrainerRequestDto {
    @NotNull
    @Size(max = 100)
    private String firstName;

    @NotNull
    @Size(max = 100)
    private String lastName;

    @NotNull
    @Size(max = 150)
    @Email
    private String email;

    @NotNull
    private String password;

    @NotNull
    private Gender gender;

    @NotNull
    private String phoneNumber;

    @NotNull
    private Role role;

    private String username;

    private LocalDate birthday;

    private String photo;

    @NotNull
    private Double height;

    @NotNull
    private Double weight;

    private LocalDate experienceStartDate;

    private LocalDate experienceEndDate;
}