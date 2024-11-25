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
public class UserDeletedEvent extends Event {
    public UserDeletedEvent() {
        this(UUID.randomUUID(), 0);
    }

    public UserDeletedEvent(UUID aggregateId, int version) {
        super(aggregateId, version);
    }
}
