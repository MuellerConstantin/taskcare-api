package de.mueller_constantin.taskcare.api.core.board.application.command;

import de.mueller_constantin.taskcare.api.core.board.domain.StatusCategory;
import de.mueller_constantin.taskcare.api.core.common.application.Command;
import de.mueller_constantin.taskcare.api.core.common.application.validation.NullOrNotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@Data
@Builder
public class AddStatusByIdCommand implements Command {
    @NotNull
    private UUID boardId;

    @NotNull
    @Size(max = 255)
    private String name;

    @NullOrNotEmpty
    @Size(max = 1024)
    private String description;

    @NotNull
    private StatusCategory category;

    public AddStatusByIdCommand(UUID boardId, String name, String description, StatusCategory category) {
        this.boardId = boardId;
        this.name = name;
        this.description = description;
        this.category = category;
    }
}
