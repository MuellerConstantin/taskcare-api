package de.mueller_constantin.taskcare.api.core.kanban.application;

import de.mueller_constantin.taskcare.api.core.common.application.NoSuchEntityException;
import de.mueller_constantin.taskcare.api.core.common.domain.Page;
import de.mueller_constantin.taskcare.api.core.common.domain.PageInfo;
import de.mueller_constantin.taskcare.api.core.kanban.application.persistence.BoardDomainRepository;
import de.mueller_constantin.taskcare.api.core.kanban.application.persistence.BoardStateRepository;
import de.mueller_constantin.taskcare.api.core.kanban.domain.BoardAggregate;
import de.mueller_constantin.taskcare.api.core.kanban.domain.BoardProjection;
import de.mueller_constantin.taskcare.api.core.user.application.ExistsUserByIdQuery;
import de.mueller_constantin.taskcare.api.core.user.application.UserService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@RequiredArgsConstructor
public class BoardService {
    private final BoardDomainRepository boardDomainRepository;
    private final BoardStateRepository boardStateRepository;
    private final UserService userService;
    private final Validator validator;

    protected void validate(Object object) throws ConstraintViolationException {
        Set<ConstraintViolation<Object>> violations = validator.validate(object);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    public void dispatch(CreateBoardCommand command) {
        validate(command);

        if(!userService.query(new ExistsUserByIdQuery(command.getCreatorId()))) {
            throw new NoSuchEntityException("User with id '" + command.getCreatorId() + "' does not exist");
        }

        BoardAggregate boardAggregate = new BoardAggregate();
        boardAggregate.create(command.getName(), command.getDescription(), command.getCreatorId());
        boardDomainRepository.save(boardAggregate);
    }

    public void dispatch(UpdateBoardByIdCommand command) {
        validate(command);

        BoardAggregate boardAggregate = boardDomainRepository.load(command.getId())
                .orElseThrow(NoSuchEntityException::new);

        String name = command.getName() != null ?
                command.getName() :
                boardAggregate.getName();

        String description = command.isDescriptionTouched() ?
                command.getDescription() :
                boardAggregate.getDescription();

        boardAggregate.update(name, description);
        boardDomainRepository.save(boardAggregate);
    }

    public void dispatch(DeleteBoardByIdCommand command) {
        validate(command);

        BoardAggregate boardAggregate = boardDomainRepository.load(command.getId())
                .orElseThrow(NoSuchEntityException::new);

        boardAggregate.delete();
        boardDomainRepository.save(boardAggregate);
    }

    public void dispatch(AddMemberByIdCommand command) {
        validate(command);

        if(!userService.query(new ExistsUserByIdQuery(command.getUserId()))) {
            throw new NoSuchEntityException("User with id '" + command.getUserId() + "' does not exist");
        }

        BoardAggregate boardAggregate = boardDomainRepository.load(command.getBoardId())
                .orElseThrow(NoSuchEntityException::new);

        boardAggregate.addMember(command.getUserId(), command.getRole());
        boardDomainRepository.save(boardAggregate);
    }

    public void dispatch(RemoveMemberByIdCommand command) {
        validate(command);

        BoardAggregate boardAggregate = boardDomainRepository.load(command.getBoardId())
                .orElseThrow(NoSuchEntityException::new);

        boardAggregate.removeMember(command.getMemberId());
        boardDomainRepository.save(boardAggregate);
    }

    public void dispatch(UpdateMemberByIdCommand command) {
        validate(command);

        BoardAggregate boardAggregate = boardDomainRepository.load(command.getBoardId())
                .orElseThrow(NoSuchEntityException::new);

        boardAggregate.updateMember(command.getMemberId(), command.getRole());
        boardDomainRepository.save(boardAggregate);
    }

    public BoardProjection query(FindBoardByIdQuery query) {
        return boardStateRepository.findById(query.getId())
                .orElseThrow(NoSuchEntityException::new);
    }

    public Page<BoardProjection> query(FindAllBoardsUserIsMemberQuery query) {
        return boardStateRepository.findAllUserIsMember(query.getUserId(), PageInfo.builder()
                .page(query.getPage())
                .perPage(query.getPerPage())
                .build());
    }

    public Page<BoardProjection> query(FindAllBoardsQuery query) {
        return boardStateRepository.findAll(PageInfo.builder()
                .page(query.getPage())
                .perPage(query.getPerPage())
                .build());
    }
}
