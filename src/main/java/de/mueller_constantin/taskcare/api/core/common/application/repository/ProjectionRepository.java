package de.mueller_constantin.taskcare.api.core.common.application.repository;

import de.mueller_constantin.taskcare.api.core.common.domain.model.Page;
import de.mueller_constantin.taskcare.api.core.common.domain.model.PageInfo;
import de.mueller_constantin.taskcare.api.core.common.domain.model.Projection;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Query optimized repository for projections. This repository is used for
 * accessing the read model.
 *
 * @param <T> The type of projection.
 */
public interface ProjectionRepository<T extends Projection> {
    Optional<T> findById(UUID id);

    List<T> findAll();

    Page<T> findAll(PageInfo pageInfo);

    boolean existsById(UUID id);
}
