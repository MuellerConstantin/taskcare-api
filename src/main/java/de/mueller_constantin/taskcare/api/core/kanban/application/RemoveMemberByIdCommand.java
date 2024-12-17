package de.mueller_constantin.taskcare.api.core.kanban.application;

import de.mueller_constantin.taskcare.api.core.common.application.Command;
import de.mueller_constantin.taskcare.api.core.kanban.domain.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@Data
@Builder
public class RemoveMemberByIdCommand implements Command {
    @NotNull
    private UUID boardId;

    @NotNull
    private UUID memberId;

    public RemoveMemberByIdCommand(UUID boardId, UUID memberId) {
        this.boardId = boardId;
        this.memberId = memberId;
    }
}
