package de.mueller_constantin.taskcare.api.infrastructure.security.token;

import org.springframework.security.core.Authentication;

public interface TokenProvider<T extends Token> {
    T generateToken(Authentication authentication);

    T validateToken(String rawToken) throws InvalidTokenException;

    void invalidateToken(String rawToken);
}
