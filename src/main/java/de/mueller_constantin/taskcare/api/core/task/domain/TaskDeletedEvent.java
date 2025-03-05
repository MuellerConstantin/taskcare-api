package de.mueller_constantin.taskcare.api.core.task.domain;

import de.mueller_constantin.taskcare.api.core.common.domain.DomainEvent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class TaskDeletedEvent extends DomainEvent {
    private final UUID boardId;

    public TaskDeletedEvent() {
        this(UUID.randomUUID(), 0, null);
    }

    public TaskDeletedEvent(UUID aggregateId, int version, UUID boardId) {
        super(aggregateId, version);
        this.boardId = boardId;
    }
}
