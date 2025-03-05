package de.mueller_constantin.taskcare.api.presentation.rest.v1.dto;

import de.mueller_constantin.taskcare.api.core.common.application.validation.Enumerated;
import de.mueller_constantin.taskcare.api.core.common.application.validation.NullOrNotEmpty;
import de.mueller_constantin.taskcare.api.core.common.application.validation.UUID;
import de.mueller_constantin.taskcare.api.core.task.domain.Priority;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CreateTaskDto {
    @NotNull
    @NotEmpty
    @Size(max = 255)
    private String name;

    private String description;
    private String statusId;
    private String assigneeId;
    private Set<String> componentIds;
    private OffsetDateTime dueDate;
    private Long estimatedEffort;
    private String priority;

    public Optional<@NullOrNotEmpty @Size(max = 1024) String> getDescription() {
        return Optional.ofNullable(this.description);
    }

    public Optional<@NullOrNotEmpty @UUID String> getStatusId() {
        return Optional.ofNullable(this.statusId);
    }

    public Optional<@NullOrNotEmpty @UUID String> getAssigneeId() {
        return Optional.ofNullable(this.assigneeId);
    }

    public Optional<Set<@UUID String>> getComponentIds() {
        return Optional.ofNullable(this.componentIds);
    }

    public Optional<@Future OffsetDateTime> getDueDate() {
        return Optional.ofNullable(this.dueDate);
    }

    public Optional<@PositiveOrZero Long> getEstimatedEffort() {
        return Optional.ofNullable(this.estimatedEffort);
    }

    public Optional<@NullOrNotEmpty @Enumerated(enumClass = Priority.class) String> getPriority() {
        return Optional.ofNullable(this.priority);
    }
}
