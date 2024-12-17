package de.mueller_constantin.taskcare.api.core.user.application;

import de.mueller_constantin.taskcare.api.core.common.application.Command;
import de.mueller_constantin.taskcare.api.core.common.application.validation.Password;
import de.mueller_constantin.taskcare.api.core.user.domain.IdentityProvider;
import de.mueller_constantin.taskcare.api.core.user.domain.Role;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CreateUserCommand implements Command {
    @NotNull
    @NotEmpty
    @Size(max = 255)
    private String username;

    @NotNull
    @NotEmpty
    @Password
    @Size(max = 64)
    private String password;

    @Size(max = 255)
    private String displayName;

    @NotNull
    private Role role;

    @NotNull
    private IdentityProvider identityProvider;
}
