package de.mueller_constantin.taskcare.api.core.user.domain;

import de.mueller_constantin.taskcare.api.core.common.domain.Projection;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter
@ToString
@EqualsAndHashCode
@SuperBuilder
public class UserProjection implements Projection {
    private final UUID id;
    private final String username;
    private final String password;
    private final String displayName;
    private final Role role;
    private final IdentityProvider identityProvider;
    private final boolean locked;

    public UserProjection(UUID id, String username, String password, String displayName, Role role,
                          IdentityProvider identityProvider, boolean locked) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.displayName = displayName;
        this.role = role;
        this.identityProvider = identityProvider;
        this.locked = locked;
    }
}
