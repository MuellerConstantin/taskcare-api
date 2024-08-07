package de.x1c1b.taskcare.api.core.user.application;

public class EmailAlreadyInUseException extends RuntimeException {

    public EmailAlreadyInUseException() {
    }

    public EmailAlreadyInUseException(String message) {
        super(message);
    }

    public EmailAlreadyInUseException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmailAlreadyInUseException(Throwable cause) {
        super(cause);
    }
}
