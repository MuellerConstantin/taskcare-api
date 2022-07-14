package de.x1c1b.taskcare.service.infrastructure.persistence.jpa.repository;

import de.x1c1b.taskcare.service.infrastructure.persistence.jpa.entity.BoardEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BoardEntityRepository extends PagingAndSortingRepository<BoardEntity, UUID>, JpaSpecificationExecutor<BoardEntity> {

    Page<BoardEntity> findAllByMembersUserUsername(String username, Pageable pageable);

    Page<BoardEntity> findAllByMembersUserUsername(String username, Specification<BoardEntity> specification, Pageable pageable);

    boolean existsByIdAndMembersUserUsername(UUID id, String username);

    boolean existsByIdAndMembersUserUsernameAndMembersRole(UUID id, String username, String role);

    boolean existsByIdAndMembersUserUsernameAndMembersRoleIn(UUID id, String username, List<String> roles);
}
