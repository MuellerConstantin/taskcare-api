package de.x1c1b.taskcare.api.core.board.application;

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
