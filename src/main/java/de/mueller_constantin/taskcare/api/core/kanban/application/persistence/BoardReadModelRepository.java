package de.mueller_constantin.taskcare.api.core.kanban.application.persistence;

import de.mueller_constantin.taskcare.api.core.common.application.ReadModelRepository;
import de.mueller_constantin.taskcare.api.core.common.domain.Page;
import de.mueller_constantin.taskcare.api.core.common.domain.PageInfo;
import de.mueller_constantin.taskcare.api.core.kanban.domain.BoardProjection;

import java.util.Optional;
import java.util.UUID;

public interface BoardReadModelRepository extends ReadModelRepository {
    Optional<BoardProjection> findById(UUID id);

    Page<BoardProjection> findAll(PageInfo pageInfo);

    Page<BoardProjection> findAllUserIsMember(UUID userId, PageInfo pageInfo);
}
