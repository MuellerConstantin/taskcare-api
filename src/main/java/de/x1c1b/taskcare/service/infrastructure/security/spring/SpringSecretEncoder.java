package de.x1c1b.taskcare.service.infrastructure.security.spring;

import de.x1c1b.taskcare.service.core.common.application.security.SecretEncoder;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@AllArgsConstructor
public class SpringSecretEncoder implements SecretEncoder {

    private final PasswordEncoder passwordEncoder;

    @Override
    public String encodeSecret(String secret) {
        return passwordEncoder.encode(secret);
    }
}
