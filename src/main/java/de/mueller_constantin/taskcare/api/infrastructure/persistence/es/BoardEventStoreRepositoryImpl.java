package de.mueller_constantin.taskcare.api.infrastructure.persistence.es;

import de.mueller_constantin.taskcare.api.core.board.application.persistence.BoardEventStoreRepository;
import de.mueller_constantin.taskcare.api.core.board.domain.*;
import de.mueller_constantin.taskcare.api.infrastructure.persistence.crud.BoardCrudRepository;
import de.mueller_constantin.taskcare.api.infrastructure.persistence.crud.ComponentCrudRepository;
import de.mueller_constantin.taskcare.api.infrastructure.persistence.crud.MemberCrudRepository;
import de.mueller_constantin.taskcare.api.infrastructure.persistence.crud.StatusCrudRepository;
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
public class BoardEventStoreRepositoryImpl implements BoardEventStoreRepository {
    private final EventStore eventStore;
    private final BoardCrudRepository boardCrudRepository;
    private final MemberCrudRepository memberCrudRepository;
    private final StatusCrudRepository statusCrudRepository;
    private final ComponentCrudRepository componentCrudRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void save(BoardAggregate aggregate) {
        eventStore.saveAggregate(aggregate);

        // Synchronize read model with event store

        if(aggregate.isDeleted()) {
            boardCrudRepository.deleteById(aggregate.getId());
        } else {
            BoardProjection boardProjection = BoardProjection.builder()
                    .id(aggregate.getId())
                    .name(aggregate.getName())
                    .description(aggregate.getDescription())
                    .build();

            List<MemberProjection> memberProjections = aggregate.getMembers().stream()
                    .map(m -> MemberProjection.builder()
                            .id(m.getId())
                            .boardId(m.getBoardId())
                            .userId(m.getUserId())
                            .role(m.getRole())
                            .build())
                    .toList();

            List<StatusProjection> statusProjections = aggregate.getStatuses().stream()
                    .map(s -> StatusProjection.builder()
                                    .id(s.getId())
                                    .boardId(s.getBoardId())
                                    .name(s.getName())
                                    .description(s.getDescription())
                                    .build())
                    .toList();

            List<ComponentProjection> componentProjections = aggregate.getComponents().stream()
                    .map(c -> ComponentProjection.builder()
                            .id(c.getId())
                            .boardId(c.getBoardId())
                            .name(c.getName())
                            .description(c.getDescription())
                            .build())
                    .toList();

            // Update board
            boardCrudRepository.save(boardProjection);

            // Delete removed members
            memberCrudRepository.deleteAllNotInIdsForBoardId(memberProjections.stream()
                    .map(MemberProjection::getId).collect(Collectors.toList()), aggregate.getId());

            // Create or update members
            memberCrudRepository.saveAllForBoardId(aggregate.getId(), memberProjections);

            // Delete removed statuses
            statusCrudRepository.deleteAllNotInIdsForBoardId(statusProjections.stream()
                    .map(StatusProjection::getId).collect(Collectors.toList()), aggregate.getId());

            // Create or update statuses
            statusCrudRepository.saveAllForBoardId(aggregate.getId(), statusProjections);

            // Delete removed components
            componentCrudRepository.deleteAllNotInIdsForBoardId(componentProjections.stream()
                    .map(ComponentProjection::getId).collect(Collectors.toList()), aggregate.getId());

            // Create or update components
            componentCrudRepository.saveAllForBoardId(aggregate.getId(), componentProjections);
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
