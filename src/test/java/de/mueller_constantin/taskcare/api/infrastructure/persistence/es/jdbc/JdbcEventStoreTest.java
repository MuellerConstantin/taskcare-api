package de.mueller_constantin.taskcare.api.infrastructure.persistence.es.jdbc;

import de.mueller_constantin.taskcare.api.core.common.domain.Event;
import de.mueller_constantin.taskcare.api.core.dummy.domain.DummyAggregate;
import de.mueller_constantin.taskcare.api.core.dummy.domain.DummyCreatedEvent;
import de.mueller_constantin.taskcare.api.core.dummy.domain.DummyUpdatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JdbcEventStoreTest {
    @Mock
    private JdbcEventStoreEventRepository eventRepository;

    @Mock
    private JdbcEventStoreSnapshotRepository snapshotRepository;

    @Mock
    private JdbcEventStoreMetadataRepository metadataRepository;

    @InjectMocks
    private JdbcEventStore jdbcEventStore;

    private UUID id;
    private List<Event> events;
    private DummyAggregate snapshot;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();

        events = List.of(
                DummyCreatedEvent.builder()
                        .aggregateId(id)
                        .version(1)
                        .property1("property1")
                        .property2("property2")
                        .build(),
                DummyUpdatedEvent.builder()
                        .aggregateId(id)
                        .version(2)
                        .property1("update1")
                        .property2("update1")
                        .build(),
                DummyUpdatedEvent.builder()
                        .aggregateId(id)
                        .version(3)
                        .property1("update2")
                        .property2("update2")
                        .build(),
                DummyUpdatedEvent.builder()
                        .aggregateId(id)
                        .version(4)
                        .property1("update3")
                        .property2("update3")
                        .build(),
                DummyUpdatedEvent.builder()
                        .aggregateId(id)
                        .version(5)
                        .property1("update4")
                        .property2("update4")
                        .build()
        );

        snapshot = new DummyAggregate(id, 0, false);
        snapshot.loadFromHistory(events.subList(0, 4));
    }

    @Test
    void loadAggregateLatestVersion() {
        when(snapshotRepository.loadSnapshot(id, null)).thenReturn(Optional.of(snapshot));
        when(eventRepository.loadEvents(id, 4, null)).thenReturn(events.subList(4, events.size()));

        Optional<DummyAggregate> aggregate = jdbcEventStore.loadAggregate(id, DummyAggregate.class, null);

        assertTrue(aggregate.isPresent());
        assertEquals(5, aggregate.get().getVersion());
        assertEquals("update4", aggregate.get().getProperty1());
        assertEquals("update4", aggregate.get().getProperty2());
    }

    @Test
    void loadAggregateSpecificVersion() {
        when(snapshotRepository.loadSnapshot(id, 2)).thenReturn(Optional.empty());
        when(eventRepository.loadEvents(id, 0, 2)).thenReturn(events.subList(0, 2));

        Optional<DummyAggregate> aggregate = jdbcEventStore.loadAggregate(id, DummyAggregate.class, 2);

        assertTrue(aggregate.isPresent());
        assertEquals(2, aggregate.get().getVersion());
        assertEquals("update1", aggregate.get().getProperty1());
        assertEquals("update1", aggregate.get().getProperty2());
    }

    @Test
    void loadAggregateExcessiveVersion() {
        when(snapshotRepository.loadSnapshot(id, 6)).thenReturn(Optional.of(snapshot));
        when(eventRepository.loadEvents(id, 4, 6)).thenReturn(events.subList(4, events.size()));

        Optional<DummyAggregate> aggregate = jdbcEventStore.loadAggregate(id, DummyAggregate.class, 6);

        assertTrue(aggregate.isPresent());
        assertEquals(5, aggregate.get().getVersion());
        assertEquals("update4", aggregate.get().getProperty1());
        assertEquals("update4", aggregate.get().getProperty2());
    }

    @Test
    void saveAggregate() {
        doNothing().when(metadataRepository).createMetadata(any());
        doNothing().when(metadataRepository).updateMetadata(any());
        doNothing().when(eventRepository).createEvent(any());
        doNothing().when(snapshotRepository).createSnapshot(any());

        jdbcEventStore.setSnapshotInterval(5);

        DummyAggregate aggregate = snapshot;
        aggregate.update("newValue1", "newValue2");

        jdbcEventStore.saveAggregate(aggregate);

        verify(metadataRepository, times(1)).createMetadata(aggregate);
        verify(eventRepository, times(1)).createEvent(any());
        verify(snapshotRepository, times(1)).createSnapshot(aggregate);
        verify(metadataRepository, times(1)).updateMetadata(aggregate);
    }
}
