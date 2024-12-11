package de.mueller_constantin.taskcare.api.core.user.application;

public class IllegalImportedUserAlterationException extends RuntimeException {
    public IllegalImportedUserAlterationException() {
    }

    public IllegalImportedUserAlterationException(String message) {
        super(message);
    }

    public IllegalImportedUserAlterationException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalImportedUserAlterationException(Throwable cause) {
        super(cause);
    }

    public IllegalImportedUserAlterationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
