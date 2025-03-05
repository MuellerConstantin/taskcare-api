package de.mueller_constantin.taskcare.api.infrastructure.security.access;

import de.mueller_constantin.taskcare.api.core.common.application.NoSuchEntityException;
import de.mueller_constantin.taskcare.api.core.board.application.BoardReadService;
import de.mueller_constantin.taskcare.api.core.board.application.query.ExistsMemberByUserIdAndBoardIdQuery;
import de.mueller_constantin.taskcare.api.core.board.application.query.FindMemberByUserIdAndBoardIdQuery;
import de.mueller_constantin.taskcare.api.core.board.domain.MemberProjection;
import de.mueller_constantin.taskcare.api.core.task.application.TaskReadService;
import de.mueller_constantin.taskcare.api.core.task.application.query.FindTaskByIdQuery;
import de.mueller_constantin.taskcare.api.core.task.domain.TaskProjection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DomainSecurityService {
    private final BoardReadService boardReadService;
    private final TaskReadService taskReadService;

    @Autowired
    public DomainSecurityService(BoardReadService boardReadService, TaskReadService taskReadService) {
        this.boardReadService = boardReadService;
        this.taskReadService = taskReadService;
    }

    public boolean isBoardMember(UUID boardId, UUID userId) {
        return boardReadService.query(ExistsMemberByUserIdAndBoardIdQuery.builder()
                .userId(userId)
                .boardId(boardId)
                .build());
    }

    public boolean isBoardMemberWithRole(UUID boardId, UUID userId, String role) {
        try {
            MemberProjection member = boardReadService.query(FindMemberByUserIdAndBoardIdQuery.builder()
                    .userId(userId)
                    .boardId(boardId)
                    .build());

            return member.getRole().toString().equals(role);
        } catch(NoSuchEntityException e) {
            return false;
        }
    }

    public boolean isBoardMemberWithAnyRole(UUID boardId, UUID userId, String... roles) {
        try {
            MemberProjection member = boardReadService.query(FindMemberByUserIdAndBoardIdQuery.builder()
                    .userId(userId)
                    .boardId(boardId)
                    .build());

            for (String role : roles) {
                if (member.getRole().toString().equals(role)) {
                    return true;
                }
            }

            return false;
        } catch(NoSuchEntityException e) {
            return false;
        }
    }

    public boolean isMemberOfTasksBoard(UUID taskId, UUID userId) {
        TaskProjection task = taskReadService.query(FindTaskByIdQuery.builder()
                .id(taskId)
                .build());

        return isBoardMember(task.getBoardId(), userId);
    }

    public boolean isMemberOfTasksBoardWithRole(UUID taskId, UUID userId, String role) {
        TaskProjection task = taskReadService.query(FindTaskByIdQuery.builder()
                .id(taskId)
                .build());

        return isBoardMemberWithRole(task.getBoardId(), userId, role);
    }
}
