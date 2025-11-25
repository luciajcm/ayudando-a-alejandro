package org.idea.fithub.security.auth.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class SignUpResponse {
    private String message;
    private String email;
    private String accessToken;
    private String refreshToken;
}
