package de.mueller_constantin.taskcare.api.core.user.domain;

import de.mueller_constantin.taskcare.api.core.common.domain.DomainEvent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class UserUnlockedEvent extends DomainEvent {
    public UserUnlockedEvent() {
        this(UUID.randomUUID(), 0);
    }

    public UserUnlockedEvent(UUID aggregateId, int version) {
        super(aggregateId, version);
    }
}
