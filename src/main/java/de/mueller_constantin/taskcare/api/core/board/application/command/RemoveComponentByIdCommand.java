package de.mueller_constantin.taskcare.api.core.board.application.command;

import de.mueller_constantin.taskcare.api.core.common.application.Command;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@Data
@Builder
public class RemoveComponentByIdCommand implements Command {
    @NotNull
    private UUID boardId;

    @NotNull
    private UUID componentId;

    public RemoveComponentByIdCommand(UUID boardId, UUID componentId) {
        this.boardId = boardId;
        this.componentId = componentId;
    }
}
