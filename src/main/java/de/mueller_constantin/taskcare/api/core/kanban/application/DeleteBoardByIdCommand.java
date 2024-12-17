package de.mueller_constantin.taskcare.api.core.kanban.application;

import de.mueller_constantin.taskcare.api.core.common.application.Command;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class DeleteBoardByIdCommand implements Command {
    @NotNull
    private UUID id;
}
