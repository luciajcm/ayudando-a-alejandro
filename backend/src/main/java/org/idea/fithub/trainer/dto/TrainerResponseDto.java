package org.idea.fithub.trainer.dto;

import lombok.Data;
import org.idea.fithub.user.domain.Gender;
import org.idea.fithub.user.domain.Role;
import org.idea.fithub.user.domain.UserStatus;

@Data
public class TrainerResponseDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Gender gender;
    private String phoneNumber;
    private Role role;
    private UserStatus userStatus;
    private String experienceTime;
}