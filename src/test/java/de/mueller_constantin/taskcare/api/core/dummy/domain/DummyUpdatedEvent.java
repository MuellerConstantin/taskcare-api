package de.mueller_constantin.taskcare.api.core.dummy.domain;

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
public class DummyUpdatedEvent extends DomainEvent {
    private final String property1;
    private final String property2;

    public DummyUpdatedEvent() {
        this(UUID.randomUUID(), 0, null, null);
    }

    public DummyUpdatedEvent(UUID aggregateId, int version, String property1, String property2) {
        super(aggregateId, version);
        this.property1 = property1;
        this.property2 = property2;
    }
}
