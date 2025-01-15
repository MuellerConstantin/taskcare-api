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
public class RemoveStatusByIdCommand implements Command {
    @NotNull
    private UUID boardId;

    @NotNull
    private UUID statusId;

    public RemoveStatusByIdCommand(UUID boardId, UUID statusId) {
        this.boardId = boardId;
        this.statusId = statusId;
    }
}
