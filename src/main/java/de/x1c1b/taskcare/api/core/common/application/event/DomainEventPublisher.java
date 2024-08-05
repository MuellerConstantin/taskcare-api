package de.x1c1b.taskcare.api.core.common.application.event;

@FunctionalInterface
public interface DomainEventPublisher {

    void publishEvent(DomainEvent domainEvent);
}
