package de.x1c1b.taskcare.api.core.board.application.command;

import de.x1c1b.taskcare.api.core.common.application.validation.NullOrNotEmpty;
import de.x1c1b.taskcare.api.core.common.application.validation.Username;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CreateTaskByIdCommand {

    private UUID id;

    @NotNull
    @NotEmpty
    @Size(max = 100)
    private String name;

    @NotEmpty
    @NotNull
    @Username
    @Size(max = 15)
    private String creator;

    private String description;
    private OffsetDateTime expiresAt;
    private Integer priority;
    private String responsible;

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
}
