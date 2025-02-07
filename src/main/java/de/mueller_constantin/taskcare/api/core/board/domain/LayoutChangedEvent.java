package de.mueller_constantin.taskcare.api.core.board.domain;

import de.mueller_constantin.taskcare.api.core.common.domain.DomainEvent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.UUID;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class LayoutChangedEvent extends DomainEvent {
    private final List<Column> columns;

    public LayoutChangedEvent() {
        this(UUID.randomUUID(), 0, null);
    }

    public LayoutChangedEvent(UUID aggregateId, int version, List<Column> columns) {
        super(aggregateId, version);
        this.columns = columns;
    }
}
