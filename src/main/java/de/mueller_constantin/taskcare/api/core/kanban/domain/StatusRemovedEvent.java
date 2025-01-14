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
public class StatusRemovedEvent extends DomainEvent {
    private final Status status;

    public StatusRemovedEvent() {
        this(UUID.randomUUID(), 0, null);
    }

    public StatusRemovedEvent(UUID aggregateId, int version, Status status) {
        super(aggregateId, version);
        this.status = status;
    }
}
