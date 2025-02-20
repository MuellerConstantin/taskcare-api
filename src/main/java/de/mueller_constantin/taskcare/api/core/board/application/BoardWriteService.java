package de.mueller_constantin.taskcare.api.core.board.application;

import de.mueller_constantin.taskcare.api.core.common.application.NoSuchEntityException;
import de.mueller_constantin.taskcare.api.core.common.application.event.DomainEventBus;
import de.mueller_constantin.taskcare.api.core.common.application.persistence.MediaStorage;
import de.mueller_constantin.taskcare.api.core.common.domain.DomainEvent;
import de.mueller_constantin.taskcare.api.core.board.application.command.*;
import de.mueller_constantin.taskcare.api.core.board.application.persistence.BoardEventStoreRepository;
import de.mueller_constantin.taskcare.api.core.board.application.persistence.BoardReadModelRepository;
import de.mueller_constantin.taskcare.api.core.board.domain.BoardAggregate;
import de.mueller_constantin.taskcare.api.core.board.domain.BoardDeletedEvent;
import de.mueller_constantin.taskcare.api.core.board.domain.BoardProjection;
import de.mueller_constantin.taskcare.api.core.board.domain.Role;
import de.mueller_constantin.taskcare.api.core.user.application.UserReadService;
import de.mueller_constantin.taskcare.api.core.user.application.query.ExistsUserByIdQuery;
import de.mueller_constantin.taskcare.api.core.user.domain.UserDeletedEvent;
import jakarta.validation.Valid;
import lombok.SneakyThrows;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;

import static lombok.Lombok.sneakyThrow;

@Service
@Validated
@Transactional
public class BoardWriteService {
    private final BoardEventStoreRepository boardEventStoreRepository;
    private final BoardReadModelRepository boardReadModelRepository;
    private final UserReadService userReadService;
    private final MediaStorage mediaStorage;
    private final DomainEventBus domainEventBus;
    private final LockRegistry lockRegistry;

    public BoardWriteService(BoardEventStoreRepository boardEventStoreRepository,
                             BoardReadModelRepository boardReadModelRepository,
                             UserReadService userReadService,
                             MediaStorage mediaStorage,
                             DomainEventBus domainEventBus,
                             LockRegistry lockRegistry) {
        this.boardEventStoreRepository = boardEventStoreRepository;
        this.boardReadModelRepository = boardReadModelRepository;
        this.userReadService = userReadService;
        this.mediaStorage = mediaStorage;
        this.domainEventBus = domainEventBus;
        this.lockRegistry = lockRegistry;

        this.domainEventBus.subscribe(UserDeletedEvent.class, this::onUserDeletedEvent);
        this.domainEventBus.subscribe(BoardDeletedEvent.class, this::onBoardDeletedEvent);
    }

    public void dispatch(@Valid CreateBoardCommand command) {
        if (!userReadService.query(new ExistsUserByIdQuery(command.getCreatorId()))) {
            throw new NoSuchEntityException("User with id '" + command.getCreatorId() + "' does not exist");
        }

        BoardAggregate boardAggregate = new BoardAggregate();
        boardAggregate.create(command.getName(), command.getDescription(), command.getCreatorId());
        boardEventStoreRepository.save(boardAggregate);
    }

    @SneakyThrows
    public void dispatch(@Valid UpdateBoardByIdCommand command) {
        lockRegistry.executeLocked(command.getId().toString(), () -> {
            BoardAggregate boardAggregate = boardEventStoreRepository.load(command.getId())
                    .orElseThrow(NoSuchEntityException::new);

            String name = command.getName() != null ?
                    command.getName() :
                    boardAggregate.getName();

            String description = command.isDescriptionTouched() ?
                    command.getDescription() :
                    boardAggregate.getDescription();

            boardAggregate.update(name, description);

            if(command.getColumns() != null) {
                boardAggregate.updateLayout(command.getColumns());
            }

            boardEventStoreRepository.save(boardAggregate);
        });
    }

    @SneakyThrows
    public void dispatch(@Valid DeleteBoardByIdCommand command) {
        lockRegistry.executeLocked(command.getId().toString(), () -> {
            BoardAggregate boardAggregate = boardEventStoreRepository.load(command.getId())
                    .orElseThrow(NoSuchEntityException::new);

            boardAggregate.delete();
            boardEventStoreRepository.save(boardAggregate);
        });
    }

    @SneakyThrows
    public void dispatch(@Valid AddMemberByIdCommand command) {
        lockRegistry.executeLocked(command.getBoardId().toString(), () -> {
            if (!userReadService.query(new ExistsUserByIdQuery(command.getUserId()))) {
                throw new NoSuchEntityException("User with id '" + command.getUserId() + "' does not exist");
            }

            BoardAggregate boardAggregate = boardEventStoreRepository.load(command.getBoardId())
                    .orElseThrow(NoSuchEntityException::new);

            boardAggregate.addMember(command.getUserId(), command.getRole());
            boardEventStoreRepository.save(boardAggregate);
        });
    }

    @SneakyThrows
    public void dispatch(@Valid RemoveMemberByIdCommand command) {
        lockRegistry.executeLocked(command.getBoardId().toString(), () -> {
            BoardAggregate boardAggregate = boardEventStoreRepository.load(command.getBoardId())
                    .orElseThrow(NoSuchEntityException::new);

            boardAggregate.removeMember(command.getMemberId());
            boardEventStoreRepository.save(boardAggregate);
        });
    }

    @SneakyThrows
    public void dispatch(@Valid UpdateMemberByIdCommand command) {
        lockRegistry.executeLocked(command.getBoardId().toString(), () -> {
            BoardAggregate boardAggregate = boardEventStoreRepository.load(command.getBoardId())
                    .orElseThrow(NoSuchEntityException::new);

            boardAggregate.updateMember(command.getMemberId(), command.getRole());
            boardEventStoreRepository.save(boardAggregate);
        });
    }

    @SneakyThrows
    public void dispatch(@Valid AddStatusByIdCommand command) {
        lockRegistry.executeLocked(command.getBoardId().toString(), () -> {
            BoardAggregate boardAggregate = boardEventStoreRepository.load(command.getBoardId())
                    .orElseThrow(NoSuchEntityException::new);

            boardAggregate.addStatus(command.getName(), command.getDescription());
            boardEventStoreRepository.save(boardAggregate);
        });
    }

    @SneakyThrows
    public void dispatch(@Valid RemoveStatusByIdCommand command) {
        lockRegistry.executeLocked(command.getBoardId().toString(), () -> {
            BoardAggregate boardAggregate = boardEventStoreRepository.load(command.getBoardId())
                    .orElseThrow(NoSuchEntityException::new);

            boardAggregate.removeStatus(command.getStatusId());
            boardEventStoreRepository.save(boardAggregate);
        });
    }

    @SneakyThrows
    public void dispatch(@Valid UpdateStatusByIdCommand command) {
        lockRegistry.executeLocked(command.getBoardId().toString(), () -> {
            BoardAggregate boardAggregate = boardEventStoreRepository.load(command.getBoardId())
                    .orElseThrow(NoSuchEntityException::new);

            String name = command.getName() != null ?
                    command.getName() :
                    boardAggregate.getStatuses().stream()
                            .filter(s -> s.getId().equals(command.getStatusId()))
                            .findFirst()
                            .orElseThrow(NoSuchEntityException::new)
                            .getName();

            String description = command.isDescriptionTouched() ?
                    command.getDescription() :
                    boardAggregate.getStatuses().stream()
                            .filter(s -> s.getId().equals(command.getStatusId()))
                            .findFirst()
                            .orElseThrow(NoSuchEntityException::new)
                            .getDescription();

            boardAggregate.updateStatus(command.getStatusId(), name, description);
            boardEventStoreRepository.save(boardAggregate);
        });
    }

    @SneakyThrows
    public void dispatch(@Valid AddComponentByIdCommand command) {
        lockRegistry.executeLocked(command.getBoardId().toString(), () -> {
            BoardAggregate boardAggregate = boardEventStoreRepository.load(command.getBoardId())
                    .orElseThrow(NoSuchEntityException::new);

            boardAggregate.addComponent(command.getName(), command.getDescription());
            boardEventStoreRepository.save(boardAggregate);
        });
    }

    @SneakyThrows
    public void dispatch(@Valid RemoveComponentByIdCommand command) {
        lockRegistry.executeLocked(command.getBoardId().toString(), () -> {
            BoardAggregate boardAggregate = boardEventStoreRepository.load(command.getBoardId())
                    .orElseThrow(NoSuchEntityException::new);

            boardAggregate.removeComponent(command.getComponentId());
            boardEventStoreRepository.save(boardAggregate);
        });
    }

    @SneakyThrows
    public void dispatch(@Valid UpdateComponentByIdCommand command) {
        lockRegistry.executeLocked(command.getBoardId().toString(), () -> {
            BoardAggregate boardAggregate = boardEventStoreRepository.load(command.getBoardId())
                    .orElseThrow(NoSuchEntityException::new);

            String name = command.getName() != null ?
                    command.getName() :
                    boardAggregate.getComponents().stream()
                            .filter(c -> c.getId().equals(command.getComponentId()))
                            .findFirst()
                            .orElseThrow(NoSuchEntityException::new)
                            .getName();

            String description = command.isDescriptionTouched() ?
                    command.getDescription() :
                    boardAggregate.getComponents().stream()
                            .filter(c -> c.getId().equals(command.getComponentId()))
                            .findFirst()
                            .orElseThrow(NoSuchEntityException::new)
                            .getDescription();

            boardAggregate.updateComponent(command.getComponentId(), name, description);
            boardEventStoreRepository.save(boardAggregate);
        });
    }

    protected void onUserDeletedEvent(DomainEvent event) {
        UserDeletedEvent userDeletedEvent = (UserDeletedEvent) event;

        List<UUID> boardIds = boardReadModelRepository.findAllUserIsMember(userDeletedEvent.getAggregateId())
                .stream()
                .map(BoardProjection::getId)
                .toList();

        boardIds.parallelStream().forEach((boardId) -> {
            try {
                lockRegistry.executeLocked(boardId.toString(), () -> {
                    BoardAggregate boardAggregate = boardEventStoreRepository.load(boardId)
                            .orElseThrow(NoSuchEntityException::new);

                    boolean onlyMember = boardAggregate.getMembers().stream()
                            .allMatch(m -> m.getUserId().equals(userDeletedEvent.getAggregateId()));

                    if (onlyMember) {
                        boardAggregate.delete();
                        boardEventStoreRepository.save(boardAggregate);
                        return;
                    }

                    boolean onlyAdmin = boardAggregate.getMembers().stream()
                            .noneMatch(m -> m.getRole() == Role.ADMINISTRATOR &&
                                    !m.getUserId().equals(userDeletedEvent.getAggregateId()));

                    if (onlyAdmin) {
                        UUID nextMemberId = boardAggregate.getMembers().stream()
                                .filter(m -> !m.getUserId().equals(userDeletedEvent.getAggregateId()))
                                .findFirst()
                                .orElseThrow(NoSuchEntityException::new)
                                .getId();

                        boardAggregate.updateMember(nextMemberId, Role.ADMINISTRATOR);
                    }

                    UUID memberId = boardAggregate.getMembers().stream()
                            .filter(m -> m.getUserId().equals(userDeletedEvent.getAggregateId()))
                            .findFirst()
                            .orElseThrow(NoSuchEntityException::new)
                            .getId();

                    boardAggregate.removeMember(memberId);
                    boardEventStoreRepository.save(boardAggregate);
                });
            } catch (InterruptedException exc) {
                throw sneakyThrow(exc);
            }
        });
    }

    protected void onBoardDeletedEvent(DomainEvent event) {
        BoardDeletedEvent boardDeletedEvent = (BoardDeletedEvent) event;

        if(mediaStorage.exists("/logo-images/" + boardDeletedEvent.getAggregateId().toString())) {
            mediaStorage.delete("/logo-images/" + boardDeletedEvent.getAggregateId().toString());
        }
    }
}
