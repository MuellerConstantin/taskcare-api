package de.x1c1b.taskcare.service.infrastructure.persistence.jpa.repository;

import de.x1c1b.taskcare.service.infrastructure.persistence.jpa.entity.UserEntity;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserEntityRepository extends PagingAndSortingRepository<UserEntity, String> {

    boolean existsByEmail(String email);
}
