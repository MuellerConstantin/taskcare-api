package de.x1c1b.taskcare.api.infrastructure.event.simp;

import de.x1c1b.taskcare.api.core.common.application.event.DomainEvent;
import de.x1c1b.taskcare.api.core.common.application.event.DomainEventPublisher;
import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@AllArgsConstructor
public class SimpDomainEventPublisher implements DomainEventPublisher {

    private final SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public void publishEvent(DomainEvent domainEvent) {
        String destination = String.format("/topic/%s", domainEvent.getChannel().replaceAll("^/+", ""));
        this.simpMessagingTemplate.convertAndSend(destination, domainEvent);
    }
}
