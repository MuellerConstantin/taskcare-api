package de.mueller_constantin.taskcare.api.infrastructure.security.access;

import de.mueller_constantin.taskcare.api.core.kanban.application.KanbanReadService;
import de.mueller_constantin.taskcare.api.core.kanban.application.query.FindBoardByIdQuery;
import de.mueller_constantin.taskcare.api.core.kanban.domain.BoardProjection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DomainSecurityService {
    private final KanbanReadService kanbanReadService;

    @Autowired
    public DomainSecurityService(KanbanReadService kanbanReadService) {
        this.kanbanReadService = kanbanReadService;
    }

    public boolean isBoardMember(UUID boardId, UUID userId) {
        BoardProjection board = kanbanReadService.query(FindBoardByIdQuery.builder()
                .id(boardId)
                .build());

        return board.getMembers().stream().anyMatch(m -> m.getUserId().equals(userId));
    }

    public boolean isBoardMemberWithRole(UUID boardId, UUID userId, String role) {
        BoardProjection board = kanbanReadService.query(FindBoardByIdQuery.builder()
                .id(boardId)
                .build());

        return board.getMembers().stream().anyMatch(m -> m.getUserId().equals(userId) && m.getRole().toString().equals(role));
    }
}
