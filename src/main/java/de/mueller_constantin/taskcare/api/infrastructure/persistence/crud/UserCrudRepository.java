package de.mueller_constantin.taskcare.api.infrastructure.persistence.crud;

import de.mueller_constantin.taskcare.api.core.common.domain.Page;
import de.mueller_constantin.taskcare.api.core.common.domain.PageInfo;
import de.mueller_constantin.taskcare.api.core.user.application.persistence.UserReadModelRepository;
import de.mueller_constantin.taskcare.api.core.user.domain.UserProjection;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserCrudRepository extends UserReadModelRepository {
    List<UserProjection> findAll();

    Page<UserProjection> findAll(PageInfo pageInfo);

    Optional<UserProjection> findById(UUID id);

    Optional<UserProjection> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsById(UUID id);

    void deleteById(UUID id);

    void save(UserProjection projection);
}
