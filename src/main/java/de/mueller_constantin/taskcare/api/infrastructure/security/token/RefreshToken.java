package de.mueller_constantin.taskcare.api.infrastructure.security.token;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RefreshToken implements Token {
    private String rawToken;
    private long expiresIn;
    private String principal;
}
