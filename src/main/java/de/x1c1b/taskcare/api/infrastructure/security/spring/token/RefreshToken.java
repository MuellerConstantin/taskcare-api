package de.x1c1b.taskcare.api.infrastructure.security.spring.token;

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
