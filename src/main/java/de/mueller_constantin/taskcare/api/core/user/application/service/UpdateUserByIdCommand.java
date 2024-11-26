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

    private boolean passwordTouched;
    private boolean displayNameTouched;
    private boolean roleTouched;

    public UpdateUserByIdCommand(UUID id,
                                 String password,
                                 String displayName,
                                 Role role,
                                 boolean passwordTouched,
                                 boolean displayNameTouched,
                                 boolean roleTouched) {
        this.id = id;
        this.password = password;
        this.displayName = displayName;
        this.role = role;
        this.passwordTouched = passwordTouched;
        this.displayNameTouched = displayNameTouched;
        this.roleTouched = roleTouched;

        if(password != null) {
            this.passwordTouched = true;
        }

        if(displayName != null) {
            this.displayNameTouched = true;
        }

        if(role != null) {
            this.roleTouched = true;
        }
    }

    public void setPassword(String password) {
        this.password = password;

        if(password != null) {
            this.passwordTouched = true;
        }
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;

        if(displayName != null) {
            this.displayNameTouched = true;
        }
    }

    public void setRole(Role role) {
        this.role = role;

        if(role != null) {
            this.roleTouched = true;
        }
    }
}
