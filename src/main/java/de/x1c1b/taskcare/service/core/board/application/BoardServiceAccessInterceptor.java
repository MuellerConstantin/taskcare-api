package de.x1c1b.taskcare.service.core.board.application;

import de.x1c1b.taskcare.service.core.board.application.command.*;
import de.x1c1b.taskcare.service.core.board.application.query.FindAllBoardsWithMembershipQuery;
import de.x1c1b.taskcare.service.core.board.application.query.FindBoardByIdQuery;
import de.x1c1b.taskcare.service.core.board.domain.BoardRepository;
import de.x1c1b.taskcare.service.core.board.domain.Role;
import de.x1c1b.taskcare.service.core.common.application.security.InsufficientPermissionsException;
import de.x1c1b.taskcare.service.core.common.application.security.PrincipalDetails;
import de.x1c1b.taskcare.service.core.common.application.security.PrincipalDetailsContext;
import lombok.AllArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import java.util.List;

/**
 * Contains domain specific access and authorization checks. Access control
 * is non-invasive using AOP and ABAC.
 */
@Aspect
@AllArgsConstructor
public class BoardServiceAccessInterceptor {

    private final PrincipalDetailsContext principalDetailsContext;
    private final BoardRepository boardRepository;

    @Before("execution(* de.x1c1b.taskcare.service.core.board.application.BoardService.execute(de.x1c1b.taskcare.service.core.board.application.command.UpdateBoardByIdCommand)) && args(command)")
    void evaluateUpdateBoardById(UpdateBoardByIdCommand command) {

        PrincipalDetails principalDetails = principalDetailsContext.getAuthenticatedPrincipal();

        // Only board administrators can change the details
        if (!boardRepository.hasMemberWithRole(command.getId(), principalDetails.getUsername(), Role.ADMINISTRATOR)) {
            throw new InsufficientPermissionsException();
        }
    }

    @Before("execution(* de.x1c1b.taskcare.service.core.board.application.BoardService.execute(de.x1c1b.taskcare.service.core.board.application.command.DeleteBoardByIdCommand)) && args(command)")
    void evaluateDeleteBoardById(DeleteBoardByIdCommand command) {

        PrincipalDetails principalDetails = principalDetailsContext.getAuthenticatedPrincipal();

        // Only board administrators can delete the board
        if (!boardRepository.hasMemberWithRole(command.getId(), principalDetails.getUsername(), Role.ADMINISTRATOR)) {
            throw new InsufficientPermissionsException();
        }
    }

    @Before("execution(* de.x1c1b.taskcare.service.core.board.application.BoardService.execute(de.x1c1b.taskcare.service.core.board.application.command.CreateMemberByIdCommand)) && args(command)")
    void evaluateAddMemberById(CreateMemberByIdCommand command) {

        PrincipalDetails principalDetails = principalDetailsContext.getAuthenticatedPrincipal();

        // Only board administrators can add board members
        if (!boardRepository.hasMemberWithRole(command.getId(), principalDetails.getUsername(), Role.ADMINISTRATOR)) {
            throw new InsufficientPermissionsException();
        }
    }

    @Before("execution(* de.x1c1b.taskcare.service.core.board.application.BoardService.execute(de.x1c1b.taskcare.service.core.board.application.command.DeleteMemberByIdCommand)) && args(command)")
    void evaluateRemoveMemberById(DeleteMemberByIdCommand command) {

        PrincipalDetails principalDetails = principalDetailsContext.getAuthenticatedPrincipal();

        // Only board administrators can remove board members
        if (!boardRepository.hasMemberWithRole(command.getId(), principalDetails.getUsername(), Role.ADMINISTRATOR)) {
            throw new InsufficientPermissionsException();
        }
    }

    @Before("execution(* de.x1c1b.taskcare.service.core.board.application.BoardService.execute(de.x1c1b.taskcare.service.core.board.application.command.UpdateMemberByIdCommand)) && args(command)")
    void evaluateUpdateMemberById(UpdateMemberByIdCommand command) {

        PrincipalDetails principalDetails = principalDetailsContext.getAuthenticatedPrincipal();

        // Only board administrators can update board members
        if (!boardRepository.hasMemberWithRole(command.getId(), principalDetails.getUsername(), Role.ADMINISTRATOR)) {
            throw new InsufficientPermissionsException();
        }
    }

    @Before("execution(* de.x1c1b.taskcare.service.core.board.application.BoardService.query(de.x1c1b.taskcare.service.core.board.application.query.FindBoardByIdQuery)) && args(query)")
    void evaluateFindById(FindBoardByIdQuery query) {

        PrincipalDetails principalDetails = principalDetailsContext.getAuthenticatedPrincipal();

        // Only board members can view the board
        if (!boardRepository.hasMember(query.getId(), principalDetails.getUsername())) {
            throw new InsufficientPermissionsException();
        }
    }

    @Before("execution(* de.x1c1b.taskcare.service.core.board.application.BoardService.query(de.x1c1b.taskcare.service.core.board.application.query.FindAllBoardsWithMembershipQuery)) && args(query)")
    void evaluateFindAllWithMembership(FindAllBoardsWithMembershipQuery query) {

        PrincipalDetails principalDetails = principalDetailsContext.getAuthenticatedPrincipal();

        // A user can only load his own list of boards
        if (!principalDetails.getUsername().equals(query.getUsername())) {
            throw new InsufficientPermissionsException();
        }
    }

    @Before("execution(* de.x1c1b.taskcare.service.core.board.application.BoardService.execute(de.x1c1b.taskcare.service.core.board.application.command.CreateTaskByIdCommand)) && args(command)")
    void evaluateAddTaskById(CreateTaskByIdCommand command) {

        PrincipalDetails principalDetails = principalDetailsContext.getAuthenticatedPrincipal();

        // Only board administrators and maintainers can add tasks
        if (!boardRepository.hasMemberWithAnyRole(command.getId(), principalDetails.getUsername(),
                List.of(Role.ADMINISTRATOR, Role.MAINTAINER))) {
            throw new InsufficientPermissionsException();
        }
    }

    @Before("execution(* de.x1c1b.taskcare.service.core.board.application.BoardService.execute(de.x1c1b.taskcare.service.core.board.application.command.DeleteTaskByIdCommand)) && args(command)")
    void evaluateRemoveTaskById(DeleteTaskByIdCommand command) {

        PrincipalDetails principalDetails = principalDetailsContext.getAuthenticatedPrincipal();

        // Only board administrators and maintainers can remove tasks
        if (!boardRepository.hasMemberWithAnyRole(command.getId(), principalDetails.getUsername(),
                List.of(Role.ADMINISTRATOR, Role.MAINTAINER))) {
            throw new InsufficientPermissionsException();
        }
    }

    @Before("execution(* de.x1c1b.taskcare.service.core.board.application.BoardService.execute(de.x1c1b.taskcare.service.core.board.application.command.UpdateTaskByIdCommand)) && args(command)")
    void evaluateUpdateTaskById(UpdateTaskByIdCommand command) {

        PrincipalDetails principalDetails = principalDetailsContext.getAuthenticatedPrincipal();

        // Only board administrators, maintainers and users can update tasks
        if (!boardRepository.hasMemberWithAnyRole(command.getId(), principalDetails.getUsername(),
                List.of(Role.ADMINISTRATOR, Role.MAINTAINER, Role.USER))) {
            throw new InsufficientPermissionsException();
        }
    }
}
