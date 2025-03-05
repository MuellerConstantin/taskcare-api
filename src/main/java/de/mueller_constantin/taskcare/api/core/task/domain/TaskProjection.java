package de.mueller_constantin.taskcare.api.core.task.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@ToString
@EqualsAndHashCode
@Builder(toBuilder = true)
public class TaskProjection {
    private UUID id;
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
}
