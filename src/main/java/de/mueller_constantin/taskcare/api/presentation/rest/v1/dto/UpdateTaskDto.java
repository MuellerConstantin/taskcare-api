package de.mueller_constantin.taskcare.api.presentation.rest.v1.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.mueller_constantin.taskcare.api.core.common.application.validation.Enumerated;
import de.mueller_constantin.taskcare.api.core.common.application.validation.NullOrNotEmpty;
import de.mueller_constantin.taskcare.api.core.common.application.validation.UUID;
import de.mueller_constantin.taskcare.api.core.task.domain.Priority;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
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
public class UpdateTaskDto {
    private String name;
    private String description;
    private String statusId;
    private String assigneeId;
    private Set<String> componentIds;
    private OffsetDateTime dueDate;
    private String priority;

    @JsonIgnore
    private boolean descriptionTouched;

    @JsonIgnore
    private boolean statusIdTouched;

    @JsonIgnore
    private boolean assigneeIdTouched;

    @JsonIgnore
    private boolean dueDateTouched;

    @JsonIgnore
    private boolean priorityTouched;

    public void setDescription(String description) {
        this.description = description;
        this.descriptionTouched = true;
    }

    public void setStatusId(String statusId) {
        this.statusId = statusId;
        this.statusIdTouched = true;
    }

    public void setAssigneeId(String assigneeId) {
        this.assigneeId = assigneeId;
        this.assigneeIdTouched = true;
    }

    public void setDueDate(OffsetDateTime dueDate) {
        this.dueDate = dueDate;
        this.dueDateTouched = true;
    }

    public void setPriority(String priority) {
        this.priority = priority;
        this.priorityTouched = true;
    }

    public Optional<@NullOrNotEmpty @Size(max = 255) String> getName() {
        return Optional.ofNullable(this.name);
    }

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

    public Optional<@NullOrNotEmpty @Enumerated(enumClass = Priority.class) String> getPriority() {
        return Optional.ofNullable(this.priority);
    }
}
