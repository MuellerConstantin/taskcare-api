package de.mueller_constantin.taskcare.api.infrastructure.security;

import de.mueller_constantin.taskcare.api.core.user.application.security.CredentialsEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SpringCredentialsEncoder implements CredentialsEncoder {
    private final PasswordEncoder passwordEncoder;

    @Override
    public String encode(String credential) {
        return passwordEncoder.encode(credential);
    }
}
