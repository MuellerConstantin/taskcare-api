package de.x1c1b.taskcare.service.core.common.application.event;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.OffsetDateTime;

@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public abstract class DomainEvent {

    private final String channel;
    private final OffsetDateTime raisedAt;
}
