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
public class ComponentRemovedEvent extends DomainEvent {
    private final Component component;

    public ComponentRemovedEvent() {
        this(UUID.randomUUID(), 0, null);
    }

    public ComponentRemovedEvent(UUID aggregateId, int version, Component component) {
        super(aggregateId, version);
        this.component = component;
    }
}
