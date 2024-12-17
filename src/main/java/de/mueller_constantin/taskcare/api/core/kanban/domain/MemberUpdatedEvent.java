package de.mueller_constantin.taskcare.api.core.kanban.domain;

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
public class MemberUpdatedEvent extends DomainEvent {
    private final UUID memberId;
    private final Role role;

    public MemberUpdatedEvent() {
        this(UUID.randomUUID(), 0, null, null);
    }

    public MemberUpdatedEvent(UUID aggregateId, int version, UUID memberId, Role role) {
        super(aggregateId, version);
        this.memberId = memberId;
        this.role = role;
    }
}
