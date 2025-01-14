package de.mueller_constantin.taskcare.api.infrastructure.persistence.crud;

import de.mueller_constantin.taskcare.api.core.common.domain.Page;
import de.mueller_constantin.taskcare.api.core.common.domain.PageInfo;
import de.mueller_constantin.taskcare.api.core.kanban.application.persistence.StatusReadModelRepository;
import de.mueller_constantin.taskcare.api.core.kanban.domain.StatusProjection;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StatusCrudRepository extends StatusReadModelRepository {
    Optional<StatusProjection> findById(UUID id);

    List<StatusProjection> findAll();

    Page<StatusProjection> findAll(PageInfo pageInfo);

    void deleteById(UUID id);

    void save(StatusProjection projection);

    /**
     * Persists a list of statuses for a board.
     *
     * @param boardId The id of the board.
     * @param projections The list of statuses to persist.
     */
    void saveAllForBoardId(UUID boardId, List<StatusProjection> projections);

    void deleteAllByBoardId(UUID boardId);

    /**
     * Deletes all statuses of a board except the ones with the given ids.
     *
     * @param ids The id list of the statuses to keep.
     * @param boardId The id of the board.
     */
    void deleteAllNotInIdsForBoardId(List<UUID> ids, UUID boardId);

    boolean existsById(UUID id);
}
