package de.mueller_constantin.taskcare.api.infrastructure.security.token.opaque;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "taskcare.security.token.refresh.opaque")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OpaqueRefreshTokenProperties {
    @Builder.Default
    private int length = 16;

    @Builder.Default
    private long expiresIn = 172800000L;
}
