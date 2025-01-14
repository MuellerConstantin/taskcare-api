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
public class StatusUpdatedEvent extends DomainEvent {
    private final UUID statusId;
    private final String name;
    private final String description;

    public StatusUpdatedEvent() {
        this(UUID.randomUUID(), 0, null, null, null);
    }

    public StatusUpdatedEvent(UUID aggregateId, int version, UUID statusId, String name, String description) {
        super(aggregateId, version);
        this.statusId = statusId;
        this.name = name;
        this.description = description;
    }
}
