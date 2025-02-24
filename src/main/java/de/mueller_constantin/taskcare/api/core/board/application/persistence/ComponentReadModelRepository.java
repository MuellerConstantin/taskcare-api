package de.mueller_constantin.taskcare.api.core.board.application.persistence;

import de.mueller_constantin.taskcare.api.core.board.domain.ComponentProjection;
import de.mueller_constantin.taskcare.api.core.common.application.ReadModelRepository;
import de.mueller_constantin.taskcare.api.core.common.domain.Page;
import de.mueller_constantin.taskcare.api.core.common.domain.PageInfo;

import java.util.Optional;
import java.util.UUID;

public interface ComponentReadModelRepository extends ReadModelRepository {
    Optional<ComponentProjection> findByIdAndBoardId(UUID id, UUID boardId);

    Page<ComponentProjection> findAllByBoardId(UUID boarId, PageInfo pageInfo);

    Page<ComponentProjection> findAllByBoardId(UUID boarId, PageInfo pageInfo, String predicate);

    boolean existsByIdAndBoardId(UUID id, UUID boardId);
}
