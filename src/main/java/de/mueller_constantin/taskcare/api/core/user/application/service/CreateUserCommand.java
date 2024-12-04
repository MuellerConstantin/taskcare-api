package de.mueller_constantin.taskcare.api.core.user.application.service;

import de.mueller_constantin.taskcare.api.core.common.application.service.Command;
import de.mueller_constantin.taskcare.api.core.user.domain.model.IdentityProvider;
import de.mueller_constantin.taskcare.api.core.user.domain.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CreateUserCommand implements Command {
    private String username;
    private String password;
    private String displayName;
    private Role role;
    private IdentityProvider identityProvider;
}
