package de.x1c1b.taskcare.api.core.board.application.command;

import de.x1c1b.taskcare.api.core.common.application.validation.NullOrNotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UpdateBoardByIdCommand {

    private UUID id;
    private String name;
    private String description;
    private boolean descriptionDirty;

    public Optional<@NullOrNotEmpty @Size(max = 100) String> getName() {
        return Optional.ofNullable(name);
    }

    public Optional<@NullOrNotEmpty @Size(max = 2000) String> getDescription() {
        return Optional.ofNullable(description);
    }
}
