package de.mueller_constantin.taskcare.api.core.task.application.persistence;

import de.mueller_constantin.taskcare.api.core.common.application.ReadModelRepository;
import de.mueller_constantin.taskcare.api.core.common.domain.Page;
import de.mueller_constantin.taskcare.api.core.common.domain.PageInfo;
import de.mueller_constantin.taskcare.api.core.task.domain.TaskProjection;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskReadModelRepository extends ReadModelRepository {
    Optional<TaskProjection> findByIdAndBoardId(UUID id, UUID boardId);

    Optional<TaskProjection> findById(UUID id);

    Page<TaskProjection> findAllByBoardId(UUID boardId, PageInfo pageInfo);

    Page<TaskProjection> findAllByBoardIdAndNoStatus(UUID boardId, PageInfo pageInfo);

    Page<TaskProjection> findAllByBoardIdAndStatusId(UUID boardId, UUID statusId, PageInfo pageInfo);

    Page<TaskProjection> findAllByBoardIdAndComponentId(UUID boardId, UUID componentId, PageInfo pageInfo);

    Page<TaskProjection> findAllByBoardIdAndAssigneeId(UUID boardId, UUID assigneeId, PageInfo pageInfo);

    List<UUID> findAllIdsByBoardId(UUID boardId);

    List<UUID> findAllIdsByBoardIdAndStatusId(UUID boardId, UUID statusId);

    List<UUID> findAllIdsByBoardIdAndComponentId(UUID boardId, UUID componentId);

    List<UUID> findAllIdsByBoardIdAndAssigneeId(UUID boardId, UUID assigneeId);
}
