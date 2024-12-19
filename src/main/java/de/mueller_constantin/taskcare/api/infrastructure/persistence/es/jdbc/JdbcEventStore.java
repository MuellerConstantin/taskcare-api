package de.mueller_constantin.taskcare.api.infrastructure.persistence.es.jdbc;

import de.mueller_constantin.taskcare.api.core.common.domain.Aggregate;
import de.mueller_constantin.taskcare.api.core.common.domain.DomainEvent;
import de.mueller_constantin.taskcare.api.infrastructure.persistence.es.EventStore;
import lombok.*;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Generic JDBC RDBMS event store implementation.
 */
@Transactional
@RequiredArgsConstructor
public class JdbcEventStore implements EventStore {
    private final JdbcEventStoreEventRepository eventRepository;
    private final JdbcEventStoreSnapshotRepository snapshotRepository;
    private final JdbcEventStoreMetadataRepository metadataRepository;

    @Getter
    @Setter
    private int snapshotInterval = 10;

    public void saveAggregate(@NonNull Aggregate aggregate) {
        metadataRepository.createMetadata(aggregate);

        for(DomainEvent event : aggregate.getUncommittedEvents()) {
            eventRepository.createEvent(event);

            if(event.getVersion() % snapshotInterval == 0) {
                snapshotRepository.createSnapshot(aggregate);
            }
        }

        metadataRepository.updateMetadata(aggregate);
    }

    public <T extends Aggregate> Optional<T> loadAggregate(@NonNull UUID aggregateId,
                                             @NonNull Class<T> aggregateClass,
                                             Integer version) {
        Aggregate aggregate = snapshotRepository.loadSnapshot(aggregateId, version)
                .orElseGet(() -> createAggregate(aggregateId, aggregateClass));

        List<DomainEvent> events = eventRepository.loadEvents(aggregateId, aggregate.getVersion(), version);

        aggregate.loadFromHistory(events);

        return aggregate.getVersion() > 0 ? Optional.of((T) aggregate) : Optional.empty();
    }

    @SneakyThrows
    private <T extends Aggregate> T createAggregate(@NonNull UUID aggregateId, @NonNull Class<T> aggregateClass) {
        Constructor<? extends Aggregate> constructor = aggregateClass.getDeclaredConstructor(UUID.class, Integer.TYPE, Boolean.TYPE);
        Aggregate aggregate = constructor.newInstance(aggregateId, 0, false);
        return (T) aggregate;
    }
}
