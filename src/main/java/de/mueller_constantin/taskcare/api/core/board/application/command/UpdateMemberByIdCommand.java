package de.mueller_constantin.taskcare.api.core.board.application.command;

import de.mueller_constantin.taskcare.api.core.common.application.Command;
import de.mueller_constantin.taskcare.api.core.board.domain.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@Data
@Builder
public class UpdateMemberByIdCommand implements Command {
    @NotNull
    private UUID boardId;

    @NotNull
    private UUID memberId;

    private Role role;

    public UpdateMemberByIdCommand(UUID boardId, UUID memberId, Role role) {
        this.boardId = boardId;
        this.memberId = memberId;
        this.role = role;
    }
}
