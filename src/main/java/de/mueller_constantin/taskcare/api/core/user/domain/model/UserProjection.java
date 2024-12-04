package de.mueller_constantin.taskcare.api.core.user.domain.model;

import de.mueller_constantin.taskcare.api.core.common.domain.model.Projection;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class UserProjection extends Projection {
    private final String username;
    private final String password;
    private final String displayName;
    private final Role role;
    private final IdentityProvider identityProvider;
    private final boolean locked;

    public UserProjection(UUID id, String username, String password, String displayName, Role role,
                          IdentityProvider identityProvider, boolean locked) {
        super(id);
        this.username = username;
        this.password = password;
        this.displayName = displayName;
        this.role = role;
        this.identityProvider = identityProvider;
        this.locked = locked;
    }
}
