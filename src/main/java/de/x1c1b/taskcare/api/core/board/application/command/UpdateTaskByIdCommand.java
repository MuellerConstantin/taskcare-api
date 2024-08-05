package de.x1c1b.taskcare.api.core.board.application.command;

import de.x1c1b.taskcare.api.core.common.application.validation.EnumValues;
import de.x1c1b.taskcare.api.core.common.application.validation.NullOrNotEmpty;
import de.x1c1b.taskcare.api.core.common.application.validation.Username;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Future;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UpdateTaskByIdCommand {

    private UUID id;
    private UUID taskId;
    private String name;
    private String description;
    private OffsetDateTime expiresAt;
    private Integer priority;
    private String responsible;
    private String status;
    private boolean descriptionDirty;
    private boolean priorityDirty;
    private boolean expiresAtDirty;
    private boolean responsibleDirty;

    public Optional<@NullOrNotEmpty @Size(max = 100) String> getName() {
        return Optional.ofNullable(name);
    }

    public Optional<@NullOrNotEmpty @Size(max = 2000) String> getDescription() {
        return Optional.ofNullable(description);
    }

    public Optional<@Future OffsetDateTime> getExpiresAt() {
        return Optional.ofNullable(expiresAt);
    }

    public Optional<@Min(0) @Max(10) Integer> getPriority() {
        return Optional.ofNullable(priority);
    }

    public Optional<@NullOrNotEmpty @Username @Size(max = 15) String> getResponsible() {
        return Optional.ofNullable(responsible);
    }

    public Optional<@NullOrNotEmpty @EnumValues(values = {"OPENED", "IN_PROGRESS", "FINISHED"}) String> getStatus() {
        return Optional.ofNullable(status);
    }
}
