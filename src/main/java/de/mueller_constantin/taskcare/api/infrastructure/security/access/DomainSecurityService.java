package de.mueller_constantin.taskcare.api.infrastructure.security.access;

import de.mueller_constantin.taskcare.api.core.common.application.NoSuchEntityException;
import de.mueller_constantin.taskcare.api.core.kanban.application.KanbanReadService;
import de.mueller_constantin.taskcare.api.core.kanban.application.query.ExistsMemberByUserIdAndBoardIdQuery;
import de.mueller_constantin.taskcare.api.core.kanban.application.query.FindMemberByUserIdAndBoardIdQuery;
import de.mueller_constantin.taskcare.api.core.kanban.domain.MemberProjection;
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
        return kanbanReadService.query(ExistsMemberByUserIdAndBoardIdQuery.builder()
                .userId(userId)
                .boardId(boardId)
                .build());
    }

    public boolean isBoardMemberWithRole(UUID boardId, UUID userId, String role) {
        try {
            MemberProjection member = kanbanReadService.query(FindMemberByUserIdAndBoardIdQuery.builder()
                    .userId(userId)
                    .boardId(boardId)
                    .build());

            return member.getRole().toString().equals(role);
        } catch(NoSuchEntityException e) {
            return false;
        }
    }
}
