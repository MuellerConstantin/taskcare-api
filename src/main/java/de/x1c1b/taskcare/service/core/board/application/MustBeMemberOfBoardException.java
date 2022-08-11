package de.x1c1b.taskcare.service.core.board.application;

public class MustBeMemberOfBoardException extends RuntimeException {

    public MustBeMemberOfBoardException() {
    }

    public MustBeMemberOfBoardException(String message) {
        super(message);
    }

    public MustBeMemberOfBoardException(String message, Throwable cause) {
        super(message, cause);
    }

    public MustBeMemberOfBoardException(Throwable cause) {
        super(cause);
    }
}
