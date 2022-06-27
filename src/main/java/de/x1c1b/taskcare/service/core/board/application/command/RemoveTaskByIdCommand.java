package de.x1c1b.taskcare.service.core.board.application.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RemoveTaskByIdCommand {

    private UUID id;
    private UUID taskId;
}
