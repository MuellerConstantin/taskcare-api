package de.mueller_constantin.taskcare.api.core.common.application.event;

import de.mueller_constantin.taskcare.api.core.common.domain.DomainEvent;

@FunctionalInterface
public interface DomainEventListener {
    void onEvent(DomainEvent event);
}
