package de.mueller_constantin.taskcare.api.core.user.application.persistence;

import de.mueller_constantin.taskcare.api.core.common.application.ReadModelRepository;
import de.mueller_constantin.taskcare.api.core.common.domain.Page;
import de.mueller_constantin.taskcare.api.core.common.domain.PageInfo;
import de.mueller_constantin.taskcare.api.core.user.domain.UserProjection;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserReadModelRepository extends ReadModelRepository {
    List<UserProjection> findAll();

    Page<UserProjection> findAll(PageInfo pageInfo);

    Optional<UserProjection> findById(UUID id);

    Optional<UserProjection> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsById(UUID id);
}
