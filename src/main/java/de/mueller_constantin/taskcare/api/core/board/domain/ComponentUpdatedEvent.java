package de.mueller_constantin.taskcare.api.core.board.domain;

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
public class ComponentUpdatedEvent extends DomainEvent {
    private final UUID componentId;
    private final String name;
    private final String description;

    public ComponentUpdatedEvent() {
        this(UUID.randomUUID(), 0, null, null, null);
    }

    public ComponentUpdatedEvent(UUID aggregateId, int version, UUID componentId, String name, String description) {
        super(aggregateId, version);
        this.componentId = componentId;
        this.name = name;
        this.description = description;
    }
}
