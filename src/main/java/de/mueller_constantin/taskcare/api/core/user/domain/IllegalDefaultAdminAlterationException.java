package de.mueller_constantin.taskcare.api.core.user.domain;

public class IllegalDefaultAdminAlterationException extends RuntimeException {
    public IllegalDefaultAdminAlterationException() {
    }

    public IllegalDefaultAdminAlterationException(String message) {
        super(message);
    }

    public IllegalDefaultAdminAlterationException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalDefaultAdminAlterationException(Throwable cause) {
        super(cause);
    }

    public IllegalDefaultAdminAlterationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
