package de.x1c1b.taskcare.api.core.board.application;

public class IsAlreadyMemberOfBoardException extends RuntimeException {

    public IsAlreadyMemberOfBoardException() {
    }

    public IsAlreadyMemberOfBoardException(String message) {
        super(message);
    }

    public IsAlreadyMemberOfBoardException(String message, Throwable cause) {
        super(message, cause);
    }

    public IsAlreadyMemberOfBoardException(Throwable cause) {
        super(cause);
    }
}
