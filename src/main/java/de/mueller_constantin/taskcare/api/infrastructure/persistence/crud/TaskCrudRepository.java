package de.mueller_constantin.taskcare.api.infrastructure.persistence.crud;

import de.mueller_constantin.taskcare.api.core.common.domain.Page;
import de.mueller_constantin.taskcare.api.core.common.domain.PageInfo;
import de.mueller_constantin.taskcare.api.core.task.application.persistence.TaskReadModelRepository;
import de.mueller_constantin.taskcare.api.core.task.domain.TaskProjection;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskCrudRepository extends TaskReadModelRepository {
    Optional<TaskProjection> findById(UUID id);

    List<TaskProjection> findAll();

    Page<TaskProjection> findAll(PageInfo pageInfo);

    void deleteById(UUID id);

    void save(TaskProjection projection);

    boolean existsById(UUID id);
}
