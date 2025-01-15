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
public class BoardUpdatedEvent extends DomainEvent {
    private final String name;
    private final String description;

    public BoardUpdatedEvent() {
        this(UUID.randomUUID(), 0, null, null);
    }

    public BoardUpdatedEvent(UUID aggregateId, int version, String name, String description) {
        super(aggregateId, version);
        this.name = name;
        this.description = description;
    }
}
