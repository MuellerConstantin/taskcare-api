package de.mueller_constantin.taskcare.api.core.user.application;

public class UsernameAlreadyInUseException extends RuntimeException {
    public UsernameAlreadyInUseException() {
    }

    public UsernameAlreadyInUseException(String message) {
        super(message);
    }

    public UsernameAlreadyInUseException(String message, Throwable cause) {
        super(message, cause);
    }

    public UsernameAlreadyInUseException(Throwable cause) {
        super(cause);
    }

    public UsernameAlreadyInUseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
