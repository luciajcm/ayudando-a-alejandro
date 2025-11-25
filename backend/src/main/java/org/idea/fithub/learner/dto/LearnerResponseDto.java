package org.idea.fithub.learner.dto;

import lombok.Data;
import org.idea.fithub.user.domain.Gender;
import org.idea.fithub.user.domain.Role;
import org.idea.fithub.user.domain.UserStatus;

@Data
public class LearnerResponseDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Gender gender;
    private String phoneNumber;
    private Role role;
    private UserStatus userStatus;
    private Double height;
    private Double weight;
    private String goal;
    private String duration;
}
