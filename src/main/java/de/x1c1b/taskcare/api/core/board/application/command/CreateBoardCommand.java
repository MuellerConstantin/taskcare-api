package de.x1c1b.taskcare.api.core.board.application.command;

import de.x1c1b.taskcare.api.core.common.application.validation.NullOrNotEmpty;
import de.x1c1b.taskcare.api.core.common.application.validation.Username;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Optional;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CreateBoardCommand {
    @NotNull
    @NotEmpty
    @Size(max = 100)
    private String name;

    private String description;

    @NotEmpty
    @NotNull
    @Username
    @Size(max = 15)
    private String creator;

    public Optional<@NullOrNotEmpty @Size(max = 2000) String> getDescription() {
        return Optional.ofNullable(description);
    }
}
