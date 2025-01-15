package de.mueller_constantin.taskcare.api.infrastructure.security.access;

import de.mueller_constantin.taskcare.api.core.common.application.NoSuchEntityException;
import de.mueller_constantin.taskcare.api.core.board.application.BoardReadService;
import de.mueller_constantin.taskcare.api.core.board.application.query.ExistsMemberByUserIdAndBoardIdQuery;
import de.mueller_constantin.taskcare.api.core.board.application.query.FindMemberByUserIdAndBoardIdQuery;
import de.mueller_constantin.taskcare.api.core.board.domain.MemberProjection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DomainSecurityService {
    private final BoardReadService boardReadService;

    @Autowired
    public DomainSecurityService(BoardReadService boardReadService) {
        this.boardReadService = boardReadService;
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
}
