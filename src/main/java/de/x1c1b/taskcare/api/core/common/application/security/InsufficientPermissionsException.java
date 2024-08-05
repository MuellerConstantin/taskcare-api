package de.x1c1b.taskcare.api.core.common.application.security;

/**
 * Is thrown if the eligibility check is not passed. Hence when the currently authenticated user
 * lacks permissions to perform an operation.
 */
public class InsufficientPermissionsException extends RuntimeException {

    public InsufficientPermissionsException() {
    }

    public InsufficientPermissionsException(String message) {
        super(message);
    }

    public InsufficientPermissionsException(String message, Throwable cause) {
        super(message, cause);
    }

    public InsufficientPermissionsException(Throwable cause) {
        super(cause);
    }
}
