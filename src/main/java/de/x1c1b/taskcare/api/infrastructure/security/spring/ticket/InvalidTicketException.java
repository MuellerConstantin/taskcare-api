package de.x1c1b.taskcare.api.infrastructure.security.spring.ticket;

import org.springframework.security.core.AuthenticationException;

public class InvalidTicketException extends AuthenticationException {

    public InvalidTicketException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public InvalidTicketException(String msg) {
        super(msg);
    }
}
