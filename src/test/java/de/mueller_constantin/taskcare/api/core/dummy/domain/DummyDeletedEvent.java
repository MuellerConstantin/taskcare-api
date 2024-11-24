package de.mueller_constantin.taskcare.api.core.dummy.domain;

import de.mueller_constantin.taskcare.api.core.common.domain.Event;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class DummyDeletedEvent extends Event {
    public DummyDeletedEvent() {
        this(UUID.randomUUID(), 0);
    }

    public DummyDeletedEvent(UUID aggregateId, int version) {
        super(aggregateId, version);
    }
}
