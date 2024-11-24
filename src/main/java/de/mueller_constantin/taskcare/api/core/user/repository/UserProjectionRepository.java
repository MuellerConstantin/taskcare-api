package de.mueller_constantin.taskcare.api.core.user.repository;

import de.mueller_constantin.taskcare.api.core.common.repository.ProjectionRepository;
import de.mueller_constantin.taskcare.api.core.user.domain.UserProjection;

import java.util.Optional;

public interface UserProjectionRepository extends ProjectionRepository<UserProjection> {
    Optional<UserProjection> findByUsername(String username);

    boolean existsByUsername(String username);
}
