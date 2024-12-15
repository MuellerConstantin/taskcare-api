package de.mueller_constantin.taskcare.api.core.user.application;

import de.mueller_constantin.taskcare.api.core.common.application.Command;
import de.mueller_constantin.taskcare.api.core.common.application.validation.NullOrNotEmpty;
import de.mueller_constantin.taskcare.api.core.common.application.validation.Password;
import de.mueller_constantin.taskcare.api.core.user.domain.Role;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@NoArgsConstructor
@Data
@Builder
public class UpdateUserByIdCommand implements Command {
    @NotNull
    private UUID id;

    @NullOrNotEmpty
    @Password
    private String password;

    private String displayName;
    private Role role;

    private boolean displayNameTouched;

    public UpdateUserByIdCommand(UUID id,
                                 String password,
                                 String displayName,
                                 Role role,
                                 boolean displayNameTouched) {
        this.id = id;
        this.password = password;
        this.displayName = displayName;
        this.role = role;
        this.displayNameTouched = displayNameTouched;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        this.displayNameTouched = true;
    }
}
