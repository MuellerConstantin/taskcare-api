package de.x1c1b.taskcare.service.core.board.application;

import de.x1c1b.taskcare.service.core.board.application.command.*;
import de.x1c1b.taskcare.service.core.board.application.query.FindAllBoardsWithMembershipQuery;
import de.x1c1b.taskcare.service.core.board.application.query.FindBoardByIdQuery;
import de.x1c1b.taskcare.service.core.board.application.query.HasBoardMemberQuery;
import de.x1c1b.taskcare.service.core.board.application.query.HasBoardMemberWithRoleQuery;
import de.x1c1b.taskcare.service.core.board.domain.*;
import de.x1c1b.taskcare.service.core.common.application.EntityNotFoundException;
import de.x1c1b.taskcare.service.core.common.domain.Page;
import de.x1c1b.taskcare.service.core.common.domain.PageSettings;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.time.OffsetDateTime;
import java.util.ArrayList;
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
        return boardRepository.hasMember(query.getId(), query.getUsername());
    }

    @Override
    public boolean query(HasBoardMemberWithRoleQuery query) {
        return boardRepository.hasMemberWithRole(query.getId(), query.getUsername(), Role.valueOf(query.getRole()));
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
    public void execute(@Valid AddMemberByIdCommand command) throws EntityNotFoundException, IsAlreadyMemberOfBoardException {
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
    public void execute(RemoveMemberByIdCommand command) throws EntityNotFoundException {
        Board board = boardRepository.findById(command.getId()).orElseThrow(EntityNotFoundException::new);

        if (board.getMembers().stream().noneMatch(member ->
                !member.getUsername().equals(command.getUsername()) &&
                        member.getRole().equals(Role.ADMINISTRATOR))) {
            // Ensures that the last admin cannot be removed and the board remains administrable
            throw new BoardMustBeAdministrableException();
        }

        if (!board.getMembers().removeIf(member -> member.getUsername().equals(command.getUsername()))) {
            throw new EntityNotFoundException();
        }

        boardRepository.save(board);
    }

    @Override
    public void execute(UpdateMemberByIdCommand command) throws EntityNotFoundException {
        Board board = boardRepository.findById(command.getId()).orElseThrow(EntityNotFoundException::new);

        command.getRole().ifPresent(role -> {

            if (board.getMembers().stream().noneMatch(member ->
                    !member.getUsername().equals(command.getUsername()) &&
                            member.getRole().equals(Role.ADMINISTRATOR))) {
                // Ensures that the last admin cannot be removed and the board remains administrable
                throw new BoardMustBeAdministrableException();
            }

            Member member = board.getMembers().stream().filter(m -> m.getUsername().equals(command.getUsername()))
                    .findFirst().orElseThrow(EntityNotFoundException::new);

            member.setRole(Role.valueOf(role));
        });

        boardRepository.save(board);
    }

    @Override
    public void execute(AddTaskByIdCommand command) throws EntityNotFoundException {
        Board board = boardRepository.findById(command.getId()).orElseThrow(EntityNotFoundException::new);

        if (null == board.getTasks()) {
            board.setTasks(new ArrayList<>());
        }

        board.getTasks().add(Task.builder()
                .id(UUID.randomUUID())
                .name(command.getName())
                .description(command.getDescription().orElse(null))
                .createdAt(OffsetDateTime.now())
                .createdBy(command.getCreator())
                .status(ProcessingStatus.OPENED)
                .expiresAt(command.getExpiresAt().orElse(null))
                .priority(command.getPriority().orElse(null))
                .responsible(command.getResponsible().orElse(null))
                .build());

        boardRepository.save(board);
    }

    @Override
    public void execute(UpdateTaskByIdCommand command) throws EntityNotFoundException {
        Board board = boardRepository.findById(command.getId()).orElseThrow(EntityNotFoundException::new);

        Task task = board.getTasks().stream().filter(t -> t.getId().equals(command.getTaskId()))
                .findFirst().orElseThrow(EntityNotFoundException::new);

        command.getName().ifPresent(task::setName);
        command.getStatus().ifPresent(status -> task.setStatus(ProcessingStatus.valueOf(status)));

        if (command.getDescription().isPresent() || command.isDescriptionDirty()) {
            task.setDescription(command.getDescription().orElse(null));
        }

        if (command.getExpiresAt().isPresent() || command.isExpiresAtDirty()) {
            task.setExpiresAt(command.getExpiresAt().orElse(null));
        }

        if (command.getPriority().isPresent() || command.isPriorityDirty()) {
            task.setPriority(command.getPriority().orElse(null));
        }

        if (command.getResponsible().isPresent() || command.isResponsibleDirty()) {
            task.setResponsible(command.getResponsible().orElse(null));
        }

        boardRepository.save(board);
    }

    @Override
    public void execute(RemoveTaskByIdCommand command) throws EntityNotFoundException {
        Board board = boardRepository.findById(command.getId()).orElseThrow(EntityNotFoundException::new);

        if (!board.getTasks().removeIf(task -> task.getId().equals(command.getTaskId()))) {
            throw new EntityNotFoundException();
        }

        boardRepository.save(board);
    }
}
