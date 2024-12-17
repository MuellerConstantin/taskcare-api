package de.mueller_constantin.taskcare.api.core.kanban.domain;

public class BoardMustBeAdministrableException extends RuntimeException {
    public BoardMustBeAdministrableException() {
    }

    public BoardMustBeAdministrableException(String message) {
        super(message);
    }

    public BoardMustBeAdministrableException(String message, Throwable cause) {
        super(message, cause);
    }

    public BoardMustBeAdministrableException(Throwable cause) {
        super(cause);
    }
}
