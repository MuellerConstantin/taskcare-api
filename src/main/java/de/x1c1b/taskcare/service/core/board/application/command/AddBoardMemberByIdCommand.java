package de.x1c1b.taskcare.service.core.board.application.command;

import de.x1c1b.taskcare.service.core.common.application.validation.EnumValues;
import de.x1c1b.taskcare.service.core.common.application.validation.Username;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AddBoardMemberByIdCommand {

    private UUID id;

    @NotEmpty
    @NotNull
    @Username
    @Size(max = 15)
    private String username;

    @NotEmpty
    @NotNull
    @EnumValues(values = {"ADMINISTRATOR", "MAINTAINER", "USER", "VISITOR"})
    private String role;
}
