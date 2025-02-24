package de.mueller_constantin.taskcare.api.core.board.domain;

import de.mueller_constantin.taskcare.api.core.common.domain.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

/**
 * The status of a task. A status is a sub-step of the workflow used
 * in the board to process tasks. A task can be assigned a status to
 * represent the corresponding work step.
 */
@Getter
@Setter(value = AccessLevel.PACKAGE)
@ToString(callSuper = true)
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class Status extends Entity {
    private UUID boardId;
    private String name;
    private String description;
    private StatusCategory category;

    public Status() {
        super(UUID.randomUUID());
    }

    public Status(UUID id, UUID boardId, String name, String description, StatusCategory category) {
        super(id);
        this.boardId = boardId;
        this.name = name;
        this.description = description;
        this.category = category;
    }
}
