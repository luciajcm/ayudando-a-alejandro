package org.idea.fithub.security.auth.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class SignInResponse {
    private String message;
    private String accessToken;
    private String refreshToken;
}
