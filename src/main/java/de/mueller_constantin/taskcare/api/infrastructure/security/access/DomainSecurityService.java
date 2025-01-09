package de.mueller_constantin.taskcare.api.infrastructure.security.access;

import de.mueller_constantin.taskcare.api.core.kanban.application.BoardService;
import de.mueller_constantin.taskcare.api.core.kanban.application.FindBoardByIdQuery;
import de.mueller_constantin.taskcare.api.core.kanban.domain.BoardProjection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DomainSecurityService {
    private final BoardService boardService;

    @Autowired
    public DomainSecurityService(BoardService boardService) {
        this.boardService = boardService;
    }

    public boolean isBoardMember(UUID boardId, UUID userId) {
        BoardProjection board = boardService.query(FindBoardByIdQuery.builder()
                .id(boardId)
                .build());

        return board.getMembers().stream().anyMatch(m -> m.getUserId().equals(userId));
    }

    public boolean isBoardMemberWithRole(UUID boardId, UUID userId, String role) {
        BoardProjection board = boardService.query(FindBoardByIdQuery.builder()
                .id(boardId)
                .build());

        return board.getMembers().stream().anyMatch(m -> m.getUserId().equals(userId) && m.getRole().toString().equals(role));
    }
}
