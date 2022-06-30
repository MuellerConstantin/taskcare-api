package de.x1c1b.taskcare.service.infrastructure.security.spring.abac;

import de.x1c1b.taskcare.service.core.board.application.command.*;
import de.x1c1b.taskcare.service.core.board.application.query.FindAllBoardsWithMembershipQuery;
import de.x1c1b.taskcare.service.core.board.application.query.FindBoardByIdQuery;
import de.x1c1b.taskcare.service.core.board.domain.BoardRepository;
import de.x1c1b.taskcare.service.core.board.domain.Role;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.List;

@Aspect
@Component
public class BoardServiceAccessInterceptor {

    private final BoardRepository boardRepository;

    @Autowired
    public BoardServiceAccessInterceptor(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    private static UserDetails extractCurrentPrinciple() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (null == authentication || !authentication.isAuthenticated()) {
            throw new InsufficientAuthenticationException("User must be authenticated");
        }

        return (UserDetails) authentication.getPrincipal();
    }

    @Before("execution(* de.x1c1b.taskcare.service.core.board.application.BoardService.execute(de.x1c1b.taskcare.service.core.board.application.command.UpdateBoardByIdCommand)) && args(command)")
    void evaluateUpdateBoardById(UpdateBoardByIdCommand command) {

        UserDetails currentPrincipal = extractCurrentPrinciple();

        // Only board administrators can change the details
        if (!boardRepository.hasMemberWithRole(command.getId(), currentPrincipal.getUsername(), Role.ADMINISTRATOR)) {
            throw new AccessDeniedException("Permissions are missing for access");
        }
    }

    @Before("execution(* de.x1c1b.taskcare.service.core.board.application.BoardService.execute(de.x1c1b.taskcare.service.core.board.application.command.DeleteBoardByIdCommand)) && args(command)")
    void evaluateDeleteBoardById(DeleteBoardByIdCommand command) {

        UserDetails currentPrincipal = extractCurrentPrinciple();

        // Only board administrators can delete the board
        if (!boardRepository.hasMemberWithRole(command.getId(), currentPrincipal.getUsername(), Role.ADMINISTRATOR)) {
            throw new AccessDeniedException("Permissions are missing for access");
        }
    }

    @Before("execution(* de.x1c1b.taskcare.service.core.board.application.BoardService.execute(de.x1c1b.taskcare.service.core.board.application.command.AddMemberByIdCommand)) && args(command)")
    void evaluateAddMemberById(AddMemberByIdCommand command) {

        UserDetails currentPrincipal = extractCurrentPrinciple();

        // Only board administrators can add board members
        if (!boardRepository.hasMemberWithRole(command.getId(), currentPrincipal.getUsername(), Role.ADMINISTRATOR)) {
            throw new AccessDeniedException("Permissions are missing for access");
        }
    }

    @Before("execution(* de.x1c1b.taskcare.service.core.board.application.BoardService.execute(de.x1c1b.taskcare.service.core.board.application.command.RemoveMemberByIdCommand)) && args(command)")
    void evaluateRemoveMemberById(RemoveMemberByIdCommand command) {

        UserDetails currentPrincipal = extractCurrentPrinciple();

        // Only board administrators can remove board members
        if (!boardRepository.hasMemberWithRole(command.getId(), currentPrincipal.getUsername(), Role.ADMINISTRATOR)) {
            throw new AccessDeniedException("Permissions are missing for access");
        }
    }

    @Before("execution(* de.x1c1b.taskcare.service.core.board.application.BoardService.execute(de.x1c1b.taskcare.service.core.board.application.command.UpdateMemberByIdCommand)) && args(command)")
    void evaluateUpdateMemberById(UpdateMemberByIdCommand command) {

        UserDetails currentPrincipal = extractCurrentPrinciple();

        // Only board administrators can update board members
        if (!boardRepository.hasMemberWithRole(command.getId(), currentPrincipal.getUsername(), Role.ADMINISTRATOR)) {
            throw new AccessDeniedException("Permissions are missing for access");
        }
    }

    @Before("execution(* de.x1c1b.taskcare.service.core.board.application.BoardService.query(de.x1c1b.taskcare.service.core.board.application.query.FindBoardByIdQuery)) && args(query)")
    void evaluateFindById(FindBoardByIdQuery query) {

        UserDetails currentPrincipal = extractCurrentPrinciple();

        // Only board members can view the board
        if (!boardRepository.hasMember(query.getId(), currentPrincipal.getUsername())) {
            throw new AccessDeniedException("Permissions are missing for access");
        }
    }

    @Before("execution(* de.x1c1b.taskcare.service.core.board.application.BoardService.query(de.x1c1b.taskcare.service.core.board.application.query.FindAllBoardsWithMembershipQuery)) && args(query)")
    void evaluateFindAllWithMembership(FindAllBoardsWithMembershipQuery query) {

        UserDetails currentPrincipal = extractCurrentPrinciple();

        // A user can only load his own list of boards
        if (!currentPrincipal.getUsername().equals(query.getUsername())) {
            throw new AccessDeniedException("Permissions are missing for access");
        }
    }

    @Before("execution(* de.x1c1b.taskcare.service.core.board.application.BoardService.execute(de.x1c1b.taskcare.service.core.board.application.command.AddTaskByIdCommand)) && args(command)")
    void evaluateAddTaskById(AddTaskByIdCommand command) {

        UserDetails currentPrincipal = extractCurrentPrinciple();

        // Only board administrators and maintainers can add tasks
        if (!boardRepository.hasMemberWithAnyRole(command.getId(), currentPrincipal.getUsername(),
                List.of(Role.ADMINISTRATOR, Role.MAINTAINER))) {
            throw new AccessDeniedException("Permissions are missing for access");
        }
    }

    @Before("execution(* de.x1c1b.taskcare.service.core.board.application.BoardService.execute(de.x1c1b.taskcare.service.core.board.application.command.RemoveTaskByIdCommand)) && args(command)")
    void evaluateRemoveTaskById(RemoveTaskByIdCommand command) {

        UserDetails currentPrincipal = extractCurrentPrinciple();

        // Only board administrators and maintainers can remove tasks
        if (!boardRepository.hasMemberWithAnyRole(command.getId(), currentPrincipal.getUsername(),
                List.of(Role.ADMINISTRATOR, Role.MAINTAINER))) {
            throw new AccessDeniedException("Permissions are missing for access");
        }
    }

    @Before("execution(* de.x1c1b.taskcare.service.core.board.application.BoardService.execute(de.x1c1b.taskcare.service.core.board.application.command.UpdateTaskByIdCommand)) && args(command)")
    void evaluateUpdateTaskById(UpdateTaskByIdCommand command) {

        UserDetails currentPrincipal = extractCurrentPrinciple();

        // Only board administrators, maintainers and users can update tasks
        if (!boardRepository.hasMemberWithAnyRole(command.getId(), currentPrincipal.getUsername(),
                List.of(Role.ADMINISTRATOR, Role.MAINTAINER, Role.USER))) {
            throw new AccessDeniedException("Permissions are missing for access");
        }
    }
}