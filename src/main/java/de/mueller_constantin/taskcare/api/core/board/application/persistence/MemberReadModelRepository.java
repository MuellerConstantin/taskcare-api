package de.mueller_constantin.taskcare.api.core.board.application.persistence;

import de.mueller_constantin.taskcare.api.core.common.application.ReadModelRepository;
import de.mueller_constantin.taskcare.api.core.common.domain.Page;
import de.mueller_constantin.taskcare.api.core.common.domain.PageInfo;
import de.mueller_constantin.taskcare.api.core.board.domain.MemberProjection;

import java.util.Optional;
import java.util.UUID;

public interface MemberReadModelRepository extends ReadModelRepository {
    Optional<MemberProjection> findByIdAndBoardId(UUID id, UUID boardId);

    Optional<MemberProjection> findByUserIdAndBoardId(UUID userId, UUID boardId);

    Page<MemberProjection> findAllByBoardId(UUID boarId, PageInfo pageInfo);

    Page<MemberProjection> findAllByBoardId(UUID boarId, PageInfo pageInfo, String predicate);

    boolean existsByUserIdAndBoardId(UUID userId, UUID boardId);
}
