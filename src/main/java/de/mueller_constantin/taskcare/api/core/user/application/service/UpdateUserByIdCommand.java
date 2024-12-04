package de.mueller_constantin.taskcare.api.core.user.application.service;

import de.mueller_constantin.taskcare.api.core.common.application.service.Command;
import de.mueller_constantin.taskcare.api.core.user.domain.model.Role;
import lombok.*;

import java.util.UUID;

@NoArgsConstructor
@Data
@Builder
public class UpdateUserByIdCommand implements Command {
    private UUID id;

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

        if(displayName != null) {
            this.displayNameTouched = true;
        }
    }
}
