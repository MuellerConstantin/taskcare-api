package de.mueller_constantin.taskcare.api.infrastructure.persistence.es;

import de.mueller_constantin.taskcare.api.core.kanban.application.persistence.BoardEventStoreRepository;
import de.mueller_constantin.taskcare.api.core.kanban.domain.BoardAggregate;
import de.mueller_constantin.taskcare.api.core.kanban.domain.BoardProjection;
import de.mueller_constantin.taskcare.api.core.kanban.domain.MemberProjection;
import de.mueller_constantin.taskcare.api.infrastructure.persistence.crud.BoardCrudRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@Transactional
@RequiredArgsConstructor
public class BoardEventStoreRepositoryImpl implements BoardEventStoreRepository {
    private final EventStore eventStore;
    private final BoardCrudRepository boardCrudRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void save(BoardAggregate aggregate) {
        eventStore.saveAggregate(aggregate);

        // Synchronize read model with event store

        if(aggregate.isDeleted()) {
            boardCrudRepository.deleteById(aggregate.getId());
        } else {
            BoardProjection projection = BoardProjection.builder()
                    .id(aggregate.getId())
                    .name(aggregate.getName())
                    .description(aggregate.getDescription())
                    .members(aggregate.getMembers().stream()
                            .map(m -> MemberProjection.builder()
                                    .id(m.getId())
                                    .userId(m.getUserId())
                                    .role(m.getRole())
                                    .build())
                            .collect(Collectors.toSet()))
                    .build();

            boardCrudRepository.save(projection);
        }

        aggregate.getUncommittedEvents().forEach(applicationEventPublisher::publishEvent);
        aggregate.commit();
    }

    @Override
    public Optional<BoardAggregate> load(UUID aggregateId) {
        return eventStore.loadAggregate(aggregateId, BoardAggregate.class, null);
    }

    @Override
    public Optional<BoardAggregate> load(UUID aggregateId, Integer version) {
        return eventStore.loadAggregate(aggregateId, BoardAggregate.class, version);
    }
}
