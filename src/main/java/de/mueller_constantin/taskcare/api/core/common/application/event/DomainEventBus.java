package de.mueller_constantin.taskcare.api.core.common.application.event;

import de.mueller_constantin.taskcare.api.core.common.domain.DomainEvent;

public interface DomainEventBus {
    <E extends DomainEvent> void subscribe(Class<E> eventType, DomainEventListener listener);

    <E extends DomainEvent> void publish(E event);
}
