package org.idea.fithub.learner.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.idea.fithub.user.domain.Gender;
import org.idea.fithub.trainer.domain.GoalType;
import org.idea.fithub.user.domain.Role;

import java.time.LocalDate;

@Data
public class LearnerRequestDto {
    @NotNull
    @Size(max = 100)
    private String firstName;

    @NotNull
    @Size(max = 100)
    private String lastName;

    @Email
    @NotNull
    @Size(max = 150)
    private String email;

    @NotNull
    private String password;

    @NotNull
    private Gender gender;

    @NotNull
    private String phoneNumber;

    @NotNull
    private Role role;

    @NotNull
    private GoalType goalType;

    private LocalDate birthday;

    private String photo;

    @NotNull
    private Double height;

    @NotNull
    private Double weight;

    private String username;
}