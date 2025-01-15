package de.mueller_constantin.taskcare.api.core.board.domain;

import de.mueller_constantin.taskcare.api.core.common.domain.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter
@Setter(value = AccessLevel.PACKAGE)
@ToString(callSuper = true)
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class Status extends Entity {
    private UUID boardId;
    private String name;
    private String description;

    public Status() {
        super(UUID.randomUUID());
    }

    public Status(UUID id, UUID boardId, String name, String description) {
        super(id);
        this.boardId = boardId;
        this.name = name;
        this.description = description;
    }
}
