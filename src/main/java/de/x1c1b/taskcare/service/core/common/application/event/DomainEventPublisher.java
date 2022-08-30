package de.x1c1b.taskcare.service.core.common.application.event;

@FunctionalInterface
public interface DomainEventPublisher {

    void publishEvent(DomainEvent domainEvent);
}
