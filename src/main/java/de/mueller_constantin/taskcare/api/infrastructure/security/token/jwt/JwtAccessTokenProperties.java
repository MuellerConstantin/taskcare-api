package de.mueller_constantin.taskcare.api.infrastructure.security.token.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "taskcare.security.token.access.jwt")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class JwtAccessTokenProperties {
    private String secret;

    @Builder.Default
    private long expiresIn = 3600000L;
}
