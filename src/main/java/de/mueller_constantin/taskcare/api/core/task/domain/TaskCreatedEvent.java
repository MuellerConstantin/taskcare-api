package de.mueller_constantin.taskcare.api.core.task.domain;

import de.mueller_constantin.taskcare.api.core.common.domain.DomainEvent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class TaskCreatedEvent extends DomainEvent {
    private UUID boardId;
    private String name;
    private String description;
    private UUID assigneeId;
    private UUID statusId;
    private OffsetDateTime statusUpdatedAt;
    private Set<UUID> componentIds;
    private OffsetDateTime dueDate;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private Priority priority;

    public TaskCreatedEvent() {
        this(UUID.randomUUID(),
                0,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
                );
    }

    public TaskCreatedEvent(UUID aggregateId,
                            int version,
                            UUID boardId,
                            String name,
                            String description,
                            UUID assigneeId,
                            UUID statusId,
                            OffsetDateTime statusUpdatedAt,
                            Set<UUID> componentIds,
                            OffsetDateTime dueDate,
                            OffsetDateTime createdAt,
                            OffsetDateTime updatedAt,
                            Priority priority) {
        super(aggregateId, version);
        this.boardId = boardId;
        this.name = name;
        this.description = description;
        this.assigneeId = assigneeId;
        this.statusId = statusId;
        this.statusUpdatedAt = statusUpdatedAt;
        this.componentIds = componentIds;
        this.dueDate = dueDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.priority = priority;
    }
}
