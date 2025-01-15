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
public class BoardDeletedEvent extends DomainEvent {
    public BoardDeletedEvent() {
        this(UUID.randomUUID(), 0);
    }

    public BoardDeletedEvent(UUID aggregateId, int version) {
        super(aggregateId, version);
    }
}
