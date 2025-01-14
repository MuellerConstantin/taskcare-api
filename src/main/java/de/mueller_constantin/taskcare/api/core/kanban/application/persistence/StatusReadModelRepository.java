package de.mueller_constantin.taskcare.api.core.kanban.application.persistence;

import de.mueller_constantin.taskcare.api.core.common.application.ReadModelRepository;
import de.mueller_constantin.taskcare.api.core.common.domain.Page;
import de.mueller_constantin.taskcare.api.core.common.domain.PageInfo;
import de.mueller_constantin.taskcare.api.core.kanban.domain.StatusProjection;

import java.util.Optional;
import java.util.UUID;

public interface StatusReadModelRepository extends ReadModelRepository {
    Optional<StatusProjection> findByIdAndBoardId(UUID id, UUID boardId);

    Page<StatusProjection> findAllByBoardId(UUID boarId, PageInfo pageInfo);
}
