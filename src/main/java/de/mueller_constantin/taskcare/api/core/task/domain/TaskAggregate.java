package de.mueller_constantin.taskcare.api.core.task.domain;

import de.mueller_constantin.taskcare.api.core.common.domain.Aggregate;
import de.mueller_constantin.taskcare.api.core.common.domain.DomainEvent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a task.
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class TaskAggregate extends Aggregate {
    private UUID boardId;
    private String name;
    private String description;
    private UUID assigneeId;
    private UUID statusId;
    private OffsetDateTime statusUpdatedAt;
    private Set<UUID> componentIds = new HashSet<>();
    private OffsetDateTime dueDate;
    private OffsetDateTime createdAt;
    private Long estimatedEffort;
    private Priority priority;

    public TaskAggregate() {
        this(UUID.randomUUID(), 0, false);
    }

    public TaskAggregate(UUID id, int version, boolean deleted) {
        super(id, version, deleted);
    }

    @Override
    protected void processEvent(DomainEvent event) throws IllegalArgumentException {
        if(event instanceof TaskCreatedEvent) {
            this.boardId = ((TaskCreatedEvent) event).getBoardId();
            this.name = ((TaskCreatedEvent) event).getName();
            this.description = ((TaskCreatedEvent) event).getDescription();
            this.assigneeId = ((TaskCreatedEvent) event).getAssigneeId();
            this.statusId = ((TaskCreatedEvent) event).getStatusId();
            this.statusUpdatedAt = ((TaskCreatedEvent) event).getStatusUpdatedAt();
            this.componentIds = ((TaskCreatedEvent) event).getComponentIds();
            this.dueDate = ((TaskCreatedEvent) event).getDueDate();
            this.createdAt = ((TaskCreatedEvent) event).getCreatedAt();
            this.estimatedEffort = ((TaskCreatedEvent) event).getEstimatedEffort();
            this.priority = ((TaskCreatedEvent) event).getPriority();
            return;
        } else if(event instanceof TaskUpdatedEvent) {
            this.name = ((TaskUpdatedEvent) event).getName();
            this.description = ((TaskUpdatedEvent) event).getDescription();
            this.assigneeId = ((TaskUpdatedEvent) event).getAssigneeId();
            this.statusId = ((TaskUpdatedEvent) event).getStatusId();
            this.statusUpdatedAt = ((TaskUpdatedEvent) event).getStatusUpdatedAt();
            this.componentIds = ((TaskUpdatedEvent) event).getComponentIds();
            this.dueDate = ((TaskUpdatedEvent) event).getDueDate();
            this.estimatedEffort = ((TaskUpdatedEvent) event).getEstimatedEffort();
            this.priority = ((TaskUpdatedEvent) event).getPriority();
            return;
        } else if (event instanceof TaskDeletedEvent) {
            return;
        }

        throw new IllegalArgumentException("Unknown event type: %s".formatted(event.getClass()));
    }

    @Override
    protected void processDelete() {
        this.applyChange(TaskDeletedEvent.builder()
                .aggregateId(this.getId())
                .version(this.getNextVersion())
                .boardId(this.getBoardId())
                .build());
    }

    public void create(UUID boardId,
                       String name,
                       String description,
                       UUID assigneeId,
                       UUID statusId,
                       Set<UUID> componentIds,
                       OffsetDateTime dueDate,
                       Long estimatedEffort,
                       Priority priority) {
        OffsetDateTime statusUpdatedAt = statusId != null ? OffsetDateTime.now() : null;
        Set<UUID> ensuredComponentIds = componentIds != null ? componentIds : new HashSet<>();

        this.applyChange(TaskCreatedEvent.builder()
                .aggregateId(this.getId())
                .version(this.getNextVersion())
                .boardId(boardId)
                .name(name)
                .description(description)
                .assigneeId(assigneeId)
                .statusId(statusId)
                .statusUpdatedAt(statusUpdatedAt)
                .componentIds(ensuredComponentIds)
                .dueDate(dueDate)
                .createdAt(OffsetDateTime.now())
                .estimatedEffort(estimatedEffort)
                .priority(priority)
                .build());
    }

    public void update(String name,
                       String description,
                       UUID assigneeId,
                       UUID statusId,
                       Set<UUID> componentIds,
                       OffsetDateTime dueDate,
                       Long estimatedEffort,
                       Priority priority) {
        OffsetDateTime statusUpdatedAt = statusId != this.statusId ? OffsetDateTime.now() : this.getStatusUpdatedAt();

        this.applyChange(TaskUpdatedEvent.builder()
                .aggregateId(this.getId())
                .version(this.getNextVersion())
                .name(name)
                .description(description)
                .assigneeId(assigneeId)
                .statusId(statusId)
                .statusUpdatedAt(statusUpdatedAt)
                .componentIds(componentIds)
                .dueDate(dueDate)
                .estimatedEffort(estimatedEffort)
                .priority(priority)
                .build());
    }
}
