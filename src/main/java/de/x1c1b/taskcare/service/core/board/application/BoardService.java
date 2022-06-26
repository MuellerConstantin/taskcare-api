package de.x1c1b.taskcare.service.core.board.application;

import de.x1c1b.taskcare.service.core.board.application.command.*;
import de.x1c1b.taskcare.service.core.board.application.query.FindAllBoardsWithMembershipQuery;
import de.x1c1b.taskcare.service.core.board.application.query.FindBoardByIdQuery;
import de.x1c1b.taskcare.service.core.board.application.query.HasBoardMemberQuery;
import de.x1c1b.taskcare.service.core.board.application.query.HasBoardMemberWithRoleQuery;
import de.x1c1b.taskcare.service.core.board.domain.Board;
import de.x1c1b.taskcare.service.core.common.application.EntityNotFoundException;
import de.x1c1b.taskcare.service.core.common.domain.Page;

import javax.validation.Valid;

public interface BoardService {

    Board query(FindBoardByIdQuery query) throws EntityNotFoundException;

    boolean query(HasBoardMemberQuery query);

    boolean query(HasBoardMemberWithRoleQuery query);

    Page<Board> query(FindAllBoardsWithMembershipQuery query);

    void execute(@Valid CreateBoardCommand command);

    void execute(@Valid UpdateBoardByIdCommand command) throws EntityNotFoundException;

    void execute(DeleteBoardByIdCommand command) throws EntityNotFoundException;

    void execute(@Valid AddBoardMemberByIdCommand command) throws EntityNotFoundException, IsAlreadyMemberOfBoardException;

    void execute(@Valid RemoveBoardMemberByIdCommand command) throws EntityNotFoundException;

    void execute(@Valid ChangeBoardMemberRoleByIdCommand command) throws EntityNotFoundException;
}
