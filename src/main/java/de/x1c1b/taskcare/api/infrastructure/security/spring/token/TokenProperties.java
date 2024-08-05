package de.x1c1b.taskcare.api.infrastructure.security.spring.token;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "taskcare.security.token")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TokenProperties {

    private AccessTokenProperties access;
    private RefreshTokenProperties refresh;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class AccessTokenProperties {

        private String secret;

        @Builder.Default
        private long expiresIn = 300000L; // 5 minutes
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class RefreshTokenProperties {

        @Builder.Default
        private int length = 16;

        @Builder.Default
        private long expiresIn = 7200000L; // 2 hours
    }
}
