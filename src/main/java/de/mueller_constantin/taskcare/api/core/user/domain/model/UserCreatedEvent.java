package de.mueller_constantin.taskcare.api.core.user.domain.model;

import de.mueller_constantin.taskcare.api.core.common.domain.model.Event;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class UserCreatedEvent extends Event {
    private final String username;
    private final String password;
    private final String displayName;
    private final Role role;
    private final IdentityProvider identityProvider;

    public UserCreatedEvent() {
        this(UUID.randomUUID(), 0, null, null, null, null, null);
    }

    public UserCreatedEvent(UUID aggregateId, int version, String username, String password, String displayName,
                            Role role, IdentityProvider identityProvider) {
        super(aggregateId, version);
        this.username = username;
        this.password = password;
        this.displayName = displayName;
        this.identityProvider = identityProvider;
        this.role = role;
    }
}
