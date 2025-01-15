package de.mueller_constantin.taskcare.api.core.board.domain;

import de.mueller_constantin.taskcare.api.core.common.domain.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

/**
 * A component is a sub-structure of a board that is used to
 * divide the contents of the board into logical parts. Tasks can
 * be assigned to components to organize them in a logical way.
 * Basically, a component is a grouping of tasks and can be used to
 * model subprojects or other logical groups of tasks.
 */
@Getter
@Setter(value = AccessLevel.PACKAGE)
@ToString(callSuper = true)
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class Component extends Entity {
    private UUID boardId;
    private String name;
    private String description;

    public Component() {
        super(UUID.randomUUID());
    }

    public Component(UUID id, UUID boardId, String name, String description) {
        super(id);
        this.boardId = boardId;
        this.name = name;
        this.description = description;
    }
}
