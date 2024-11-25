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
public class UserUpdatedEvent extends Event {
    private final String password;
    private final String displayName;
    private final Role role;

    public UserUpdatedEvent() {
        this(UUID.randomUUID(), 0, null, null, null);
    }

    public UserUpdatedEvent(UUID aggregateId, int version, String password, String displayName, Role role) {
        super(aggregateId, version);
        this.password = password;
        this.displayName = displayName;
        this.role = role;
    }
}
