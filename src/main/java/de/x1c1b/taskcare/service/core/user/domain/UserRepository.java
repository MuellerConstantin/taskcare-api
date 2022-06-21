package de.x1c1b.taskcare.service.core.user.domain;

import de.x1c1b.taskcare.service.core.common.domain.Repository;

public interface UserRepository extends Repository<String, User> {

    boolean existsByEmail(String email);
}
