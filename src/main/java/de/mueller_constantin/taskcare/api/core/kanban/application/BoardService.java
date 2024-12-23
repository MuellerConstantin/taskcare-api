package de.mueller_constantin.taskcare.api.core.kanban.application;

import de.mueller_constantin.taskcare.api.core.common.application.NoSuchEntityException;
import de.mueller_constantin.taskcare.api.core.common.application.event.DomainEventBus;
import de.mueller_constantin.taskcare.api.core.common.domain.DomainEvent;
import de.mueller_constantin.taskcare.api.core.common.domain.Page;
import de.mueller_constantin.taskcare.api.core.common.domain.PageInfo;
import de.mueller_constantin.taskcare.api.core.kanban.application.persistence.BoardEventStoreRepository;
import de.mueller_constantin.taskcare.api.core.kanban.application.persistence.BoardReadModelRepository;
import de.mueller_constantin.taskcare.api.core.kanban.domain.BoardAggregate;
import de.mueller_constantin.taskcare.api.core.kanban.domain.BoardProjection;
import de.mueller_constantin.taskcare.api.core.kanban.domain.Role;
import de.mueller_constantin.taskcare.api.core.user.application.ExistsUserByIdQuery;
import de.mueller_constantin.taskcare.api.core.user.application.UserService;
import de.mueller_constantin.taskcare.api.core.user.domain.UserDeletedEvent;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BoardService {
    private final BoardEventStoreRepository boardEventStoreRepository;
    private final BoardReadModelRepository boardReadModelRepository;
    private final UserService userService;
    private final DomainEventBus domainEventBus;
    private final Validator validator;

    public BoardService(BoardEventStoreRepository boardEventStoreRepository,
                        BoardReadModelRepository boardReadModelRepository,
                        UserService userService,
                        DomainEventBus domainEventBus,
                        Validator validator) {
        this.boardEventStoreRepository = boardEventStoreRepository;
        this.boardReadModelRepository = boardReadModelRepository;
        this.userService = userService;
        this.domainEventBus = domainEventBus;
        this.validator = validator;

        this.domainEventBus.subscribe(UserDeletedEvent.class, this::onUserDeletedEvent);
    }

    protected void validate(Object object) throws ConstraintViolationException {
        Set<ConstraintViolation<Object>> violations = validator.validate(object);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    public void dispatch(CreateBoardCommand command) {
        validate(command);

        if (!userService.query(new ExistsUserByIdQuery(command.getCreatorId()))) {
            throw new NoSuchEntityException("User with id '" + command.getCreatorId() + "' does not exist");
        }

        BoardAggregate boardAggregate = new BoardAggregate();
        boardAggregate.create(command.getName(), command.getDescription(), command.getCreatorId());
        boardEventStoreRepository.save(boardAggregate);
    }

    public void dispatch(UpdateBoardByIdCommand command) {
        validate(command);

        BoardAggregate boardAggregate = boardEventStoreRepository.load(command.getId())
                .orElseThrow(NoSuchEntityException::new);

        String name = command.getName() != null ?
                command.getName() :
                boardAggregate.getName();

        String description = command.isDescriptionTouched() ?
                command.getDescription() :
                boardAggregate.getDescription();

        boardAggregate.update(name, description);
        boardEventStoreRepository.save(boardAggregate);
    }

    public void dispatch(DeleteBoardByIdCommand command) {
        validate(command);

        BoardAggregate boardAggregate = boardEventStoreRepository.load(command.getId())
                .orElseThrow(NoSuchEntityException::new);

        boardAggregate.delete();
        boardEventStoreRepository.save(boardAggregate);
    }

    public void dispatch(AddMemberByIdCommand command) {
        validate(command);

        if (!userService.query(new ExistsUserByIdQuery(command.getUserId()))) {
            throw new NoSuchEntityException("User with id '" + command.getUserId() + "' does not exist");
        }

        BoardAggregate boardAggregate = boardEventStoreRepository.load(command.getBoardId())
                .orElseThrow(NoSuchEntityException::new);

        boardAggregate.addMember(command.getUserId(), command.getRole());
        boardEventStoreRepository.save(boardAggregate);
    }

    public void dispatch(RemoveMemberByIdCommand command) {
        validate(command);

        BoardAggregate boardAggregate = boardEventStoreRepository.load(command.getBoardId())
                .orElseThrow(NoSuchEntityException::new);

        boardAggregate.removeMember(command.getMemberId());
        boardEventStoreRepository.save(boardAggregate);
    }

    public void dispatch(UpdateMemberByIdCommand command) {
        validate(command);

        BoardAggregate boardAggregate = boardEventStoreRepository.load(command.getBoardId())
                .orElseThrow(NoSuchEntityException::new);

        boardAggregate.updateMember(command.getMemberId(), command.getRole());
        boardEventStoreRepository.save(boardAggregate);
    }

    public BoardProjection query(FindBoardByIdQuery query) {
        return boardReadModelRepository.findById(query.getId())
                .orElseThrow(NoSuchEntityException::new);
    }

    public Page<BoardProjection> query(FindAllBoardsUserIsMemberQuery query) {
        return boardReadModelRepository.findAllUserIsMember(query.getUserId(), PageInfo.builder()
                .page(query.getPage())
                .perPage(query.getPerPage())
                .build());
    }

    public Page<BoardProjection> query(FindAllBoardsQuery query) {
        return boardReadModelRepository.findAll(PageInfo.builder()
                .page(query.getPage())
                .perPage(query.getPerPage())
                .build());
    }

    protected void onUserDeletedEvent(DomainEvent event) {
        UserDeletedEvent userDeletedEvent = (UserDeletedEvent) event;

        List<UUID> boardIds = boardReadModelRepository.findAllUserIsMember(userDeletedEvent.getAggregateId())
                .stream()
                .map(BoardProjection::getId)
                .toList();

        boardIds.parallelStream().forEach(boardId -> {
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
    }
}
