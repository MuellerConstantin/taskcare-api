package de.x1c1b.taskcare.api.core.user.application.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class DeleteUserByUsernameCommand {

    private String username;
}
