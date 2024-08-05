package de.x1c1b.taskcare.api.infrastructure.persistence.jpa.repository;

import de.x1c1b.taskcare.api.infrastructure.persistence.jpa.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserEntityRepository extends PagingAndSortingRepository<UserEntity, String>, JpaSpecificationExecutor<UserEntity> {

    boolean existsByEmail(String email);
}
