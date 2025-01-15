package de.mueller_constantin.taskcare.api.core.board.domain;

public class BoardMemberAlreadyExistsException extends RuntimeException {
    public BoardMemberAlreadyExistsException() {
    }

    public BoardMemberAlreadyExistsException(String message) {
        super(message);
    }

    public BoardMemberAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public BoardMemberAlreadyExistsException(Throwable cause) {
        super(cause);
    }

    public BoardMemberAlreadyExistsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
