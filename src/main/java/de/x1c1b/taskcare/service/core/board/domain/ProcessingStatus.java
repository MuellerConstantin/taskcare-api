package de.x1c1b.taskcare.service.core.board.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ProcessingStatus {

    OPENED("OPENED"),
    IN_PROGRESS("IN_PROGRESS"),
    FINISHED("FINISHED");

    private final String name;
}
