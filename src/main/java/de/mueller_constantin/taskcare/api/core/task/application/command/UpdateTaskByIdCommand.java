package de.mueller_constantin.taskcare.api.core.task.application.command;

import de.mueller_constantin.taskcare.api.core.common.application.Command;
import de.mueller_constantin.taskcare.api.core.common.application.validation.NullOrNotEmpty;
import de.mueller_constantin.taskcare.api.core.task.domain.Priority;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UpdateTaskByIdCommand implements Command {
    @NotNull
    private UUID id;

    @NullOrNotEmpty
    @Size(max = 255)
    private String name;

    @NullOrNotEmpty
    @Size(max = 1024)
    private String description;

    private UUID assigneeId;
    private UUID statusId;
    private Set<UUID> componentIds;

    @Future
    private OffsetDateTime dueDate;

    private Long estimatedEffort;
    private Priority priority;

    private boolean descriptionTouched;
    private boolean assigneeIdTouched;
    private boolean statusIdTouched;
    private boolean dueDateTouched;
    private boolean estimatedEffortTouched;
    private boolean priorityTouched;

    public void setDescription(String description) {
        this.description = description;
        this.descriptionTouched = true;
    }

    public void setAssigneeId(UUID assigneeId) {
        this.assigneeId = assigneeId;
        this.assigneeIdTouched = true;
    }

    public void setStatusId(UUID statusId) {
        this.statusId = statusId;
        this.statusIdTouched = true;
    }

    public void setDueDate(OffsetDateTime dueDate) {
        this.dueDate = dueDate;
        this.dueDateTouched = true;
    }

    public void setEstimatedEffort(Long estimatedEffort) {
        this.estimatedEffort = estimatedEffort;
        this.estimatedEffortTouched = true;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
        this.priorityTouched = true;
    }
}
