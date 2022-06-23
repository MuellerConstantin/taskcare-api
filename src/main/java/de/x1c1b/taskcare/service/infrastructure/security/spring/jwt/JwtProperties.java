package de.x1c1b.taskcare.service.infrastructure.security.spring.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "taskcare.security.jwt")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class JwtProperties {

    private String secret;
    private int expiresIn;
}
