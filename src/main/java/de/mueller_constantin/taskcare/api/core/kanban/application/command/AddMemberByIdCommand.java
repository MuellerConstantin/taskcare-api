package de.mueller_constantin.taskcare.api.core.kanban.application.command;

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
public class AddMemberByIdCommand implements Command {
    @NotNull
    private UUID boardId;

    @NotNull
    private UUID userId;

    @NotNull
    private Role role;

    public AddMemberByIdCommand(UUID boardId, UUID userId, Role role) {
        this.boardId = boardId;
        this.userId = userId;
        this.role = role;
    }
}
