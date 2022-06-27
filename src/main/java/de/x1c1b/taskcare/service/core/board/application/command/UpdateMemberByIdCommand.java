package de.x1c1b.taskcare.service.core.board.application.command;

import de.x1c1b.taskcare.service.core.common.application.validation.EnumValues;
import de.x1c1b.taskcare.service.core.common.application.validation.NullOrNotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UpdateMemberByIdCommand {

    private UUID id;
    private String username;
    private String role;

    public Optional<@NullOrNotEmpty @EnumValues(values = {"ADMINISTRATOR", "MAINTAINER", "USER", "VISITOR"}) String> getRole() {
        return Optional.ofNullable(role);
    }
}
