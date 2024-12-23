package de.mueller_constantin.taskcare.api.infrastructure.persistence.crud;

import de.mueller_constantin.taskcare.api.core.common.domain.Page;
import de.mueller_constantin.taskcare.api.core.common.domain.PageInfo;
import de.mueller_constantin.taskcare.api.core.kanban.domain.BoardProjection;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BoardCrudRepository {
    Optional<BoardProjection> findById(UUID id);

    List<BoardProjection> findAll();

    Page<BoardProjection> findAll(PageInfo pageInfo);

    Page<BoardProjection> findAllUserIsMember(UUID userId, PageInfo pageInfo);

    List<BoardProjection> findAllUserIsMember(UUID userId);

    void deleteById(UUID id);

    void save(BoardProjection projection);

    boolean existsById(UUID id);
}
