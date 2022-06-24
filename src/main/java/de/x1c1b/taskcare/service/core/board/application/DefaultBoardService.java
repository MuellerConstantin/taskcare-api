package de.x1c1b.taskcare.service.core.board.application;

import de.x1c1b.taskcare.service.core.board.application.command.*;
import de.x1c1b.taskcare.service.core.board.application.query.FindAllBoardsWithMembershipQuery;
import de.x1c1b.taskcare.service.core.board.application.query.FindBoardByIdQuery;
import de.x1c1b.taskcare.service.core.board.application.query.HasBoardMemberQuery;
import de.x1c1b.taskcare.service.core.board.application.query.HasBoardMemberWithRoleQuery;
import de.x1c1b.taskcare.service.core.board.domain.Board;
import de.x1c1b.taskcare.service.core.board.domain.BoardRepository;
import de.x1c1b.taskcare.service.core.board.domain.Member;
import de.x1c1b.taskcare.service.core.board.domain.Role;
import de.x1c1b.taskcare.service.core.common.application.EntityNotFoundException;
import de.x1c1b.taskcare.service.core.common.domain.Page;
import de.x1c1b.taskcare.service.core.common.domain.PageSettings;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@Validated
public class DefaultBoardService implements BoardService {
    private final BoardRepository boardRepository;

    @Override
    public Board query(FindBoardByIdQuery query) throws EntityNotFoundException {
        return boardRepository.findById(query.getId()).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public boolean query(HasBoardMemberQuery query) {
        return boardRepository.hasMember(query.getUsername());
    }

    @Override
    public boolean query(HasBoardMemberWithRoleQuery query) {
        return boardRepository.hasMemberWithRole(query.getUsername(), Role.valueOf(query.getRole()));
    }

    @Override
    public Page<Board> query(FindAllBoardsWithMembershipQuery query) {
        return boardRepository.findAllWithMembership(query.getUsername(), PageSettings.builder()
                .page(query.getPage())
                .perPage(query.getPerPage())
                .build());
    }

    @Override
    public void execute(CreateBoardCommand command) {
        Board board = Board.builder()
                .id(UUID.randomUUID())
                .name(command.getName())
                .description(command.getDescription().orElse(null))
                .createdAt(OffsetDateTime.now())
                .createdBy(command.getCreator())
                .members(Set.of(new Member(command.getCreator(), Role.ADMINISTRATOR)))
                .build();

        boardRepository.save(board);
    }

    @Override
    public void execute(@Valid UpdateBoardByIdCommand command) throws EntityNotFoundException {
        Board board = boardRepository.findById(command.getId()).orElseThrow(EntityNotFoundException::new);

        command.getName().ifPresent(board::setName);

        if (command.getDescription().isPresent() || command.isDescriptionDirty()) {
            board.setDescription(command.getDescription().orElse(null));
        }

        boardRepository.save(board);
    }

    @Override
    public void execute(DeleteBoardByIdCommand command) throws EntityNotFoundException {
        if (!boardRepository.deleteById(command.getId())) {
            throw new EntityNotFoundException();
        }
    }

    @Override
    public void execute(@Valid AddBoardMemberByIdCommand command) throws EntityNotFoundException, IsAlreadyMemberOfBoardException {
        Board board = boardRepository.findById(command.getId()).orElseThrow(EntityNotFoundException::new);

        boolean alreadyContainsMember = board.getMembers().stream()
                .anyMatch(member -> member.getUsername().equals(command.getUsername()));

        if (alreadyContainsMember) {
            throw new IsAlreadyMemberOfBoardException();
        }

        if (null == board.getMembers()) {
            board.setMembers(new HashSet<>());
        }

        board.getMembers().add(new Member(command.getUsername(), Role.valueOf(command.getRole())));

        boardRepository.save(board);
    }

    @Override
    public void execute(@Valid RemoveBoardMemberByIdCommand command) throws EntityNotFoundException {
        Board board = boardRepository.findById(command.getId()).orElseThrow(EntityNotFoundException::new);

        if (null == board.getMembers() || board.getMembers().removeIf(member -> member.getUsername().equals(command.getUsername()))) {
            throw new EntityNotFoundException();
        }
    }
}
