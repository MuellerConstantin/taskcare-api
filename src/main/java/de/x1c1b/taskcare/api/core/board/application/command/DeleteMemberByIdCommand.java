package de.x1c1b.taskcare.api.core.board.application.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class DeleteMemberByIdCommand {

    private UUID id;
    private String username;
}
