package de.mueller_constantin.taskcare.api.infrastructure.persistence.crud;

import de.mueller_constantin.taskcare.api.core.common.domain.Page;
import de.mueller_constantin.taskcare.api.core.common.domain.PageInfo;
import de.mueller_constantin.taskcare.api.core.kanban.application.persistence.MemberReadModelRepository;
import de.mueller_constantin.taskcare.api.core.kanban.domain.MemberProjection;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MemberCrudRepository extends MemberReadModelRepository {
    Optional<MemberProjection> findById(UUID id);

    List<MemberProjection> findAll();

    Page<MemberProjection> findAll(PageInfo pageInfo);

    void deleteById(UUID id);

    void save(MemberProjection projection);

    /**
     * Persists a list of members for a board.
     *
     * @param boardId The id of the board.
     * @param projections The list of members to persist.
     */
    void saveAllForBoardId(UUID boardId, List<MemberProjection> projections);

    boolean existsById(UUID id);

    void deleteAllByBoardId(UUID boardId);

    /**
     * Deletes all members of a board except the ones with the given ids.
     *
     * @param ids The id list of the members to keep.
     * @param boardId The id of the board.
     */
    void deleteAllNotInIdsForBoardId(List<UUID> ids, UUID boardId);
}
