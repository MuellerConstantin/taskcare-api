package de.mueller_constantin.taskcare.api.infrastructure.event;

import de.mueller_constantin.taskcare.api.core.common.application.event.DomainEventBus;
import de.mueller_constantin.taskcare.api.core.common.application.event.DomainEventListener;
import de.mueller_constantin.taskcare.api.core.common.domain.DomainEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SpringDomainEventBus implements DomainEventBus {
    private final Map<Class<? extends DomainEvent>, List<DomainEventListener>> listeners = new HashMap<>();
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public <E extends DomainEvent> void subscribe(Class<E> eventType, DomainEventListener listener) {
        listeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(listener);
    }

    @Override
    public <E extends DomainEvent> void publish(E event) {
        applicationEventPublisher.publishEvent(event);
    }

    @EventListener
    public void onEvent(DomainEvent event) {
        listeners.getOrDefault(event.getClass(), List.of())
                .forEach(listener -> {
                    listener.onEvent(event);
                });
    }
}
