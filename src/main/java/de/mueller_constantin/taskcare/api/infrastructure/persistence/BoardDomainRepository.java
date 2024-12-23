package de.mueller_constantin.taskcare.api.infrastructure.persistence;

import de.mueller_constantin.taskcare.api.core.common.domain.Page;
import de.mueller_constantin.taskcare.api.core.common.domain.PageInfo;
import de.mueller_constantin.taskcare.api.core.kanban.application.persistence.BoardEventStoreRepository;
import de.mueller_constantin.taskcare.api.core.kanban.application.persistence.BoardReadModelRepository;
import de.mueller_constantin.taskcare.api.core.kanban.domain.BoardAggregate;
import de.mueller_constantin.taskcare.api.core.kanban.domain.BoardProjection;
import de.mueller_constantin.taskcare.api.core.kanban.domain.MemberProjection;
import de.mueller_constantin.taskcare.api.infrastructure.persistence.crud.BoardCrudRepository;
import de.mueller_constantin.taskcare.api.infrastructure.persistence.es.EventStore;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@Transactional
@RequiredArgsConstructor
public class BoardDomainRepository implements BoardEventStoreRepository, BoardReadModelRepository {
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

    @Override
    public Optional<BoardProjection> findById(UUID id) {
        return boardCrudRepository.findById(id);
    }

    @Override
    public List<BoardProjection> findAll() {
        return boardCrudRepository.findAll();
    }

    @Override
    public Page<BoardProjection> findAll(PageInfo pageInfo) {
        return boardCrudRepository.findAll(pageInfo);
    }

    @Override
    public Page<BoardProjection> findAllUserIsMember(UUID userId, PageInfo pageInfo) {
        return boardCrudRepository.findAllUserIsMember(userId, pageInfo);
    }

    @Override
    public boolean existsById(UUID id) {
        return boardCrudRepository.existsById(id);
    }
}
