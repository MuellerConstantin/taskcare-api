package de.x1c1b.taskcare.api.infrastructure.security.spring.token;

import org.springframework.security.core.AuthenticationException;

public class InvalidTokenException extends AuthenticationException {

    public InvalidTokenException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public InvalidTokenException(String msg) {
        super(msg);
    }
}
