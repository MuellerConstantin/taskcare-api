package de.x1c1b.taskcare.service.infrastructure.security.spring.token;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AccessToken implements Token {

    private String rawToken;
    private long expiresIn;
    private String principal;
}
