package de.x1c1b.taskcare.service.infrastructure.persistence.jpa.repository;

import de.x1c1b.taskcare.service.infrastructure.persistence.jpa.entity.MemberEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MemberEntityRepository extends CrudRepository<MemberEntity, MemberEntity.MemberEntityId> {

    boolean existsByBoardIdAndUserUsername(UUID id, String username);

    boolean existsByBoardIdAndUserUsernameAndRole(UUID id, String username, String role);
}
