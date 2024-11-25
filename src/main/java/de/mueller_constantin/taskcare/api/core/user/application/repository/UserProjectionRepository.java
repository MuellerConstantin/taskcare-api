package de.mueller_constantin.taskcare.api.core.user.application.repository;

import de.mueller_constantin.taskcare.api.core.common.application.repository.ProjectionRepository;
import de.mueller_constantin.taskcare.api.core.user.domain.model.UserProjection;

import java.util.Optional;

public interface UserProjectionRepository extends ProjectionRepository<UserProjection> {
    Optional<UserProjection> findByUsername(String username);

    boolean existsByUsername(String username);
}
