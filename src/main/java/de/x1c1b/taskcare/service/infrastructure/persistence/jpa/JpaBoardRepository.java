package de.x1c1b.taskcare.service.infrastructure.persistence.jpa;

import de.x1c1b.taskcare.service.core.board.domain.Board;
import de.x1c1b.taskcare.service.core.board.domain.BoardRepository;
import de.x1c1b.taskcare.service.core.board.domain.Role;
import de.x1c1b.taskcare.service.core.common.domain.Page;
import de.x1c1b.taskcare.service.core.common.domain.PageSettings;
import de.x1c1b.taskcare.service.infrastructure.persistence.jpa.entity.BoardEntity;
import de.x1c1b.taskcare.service.infrastructure.persistence.jpa.entity.mapper.BoardEntityMapper;
import de.x1c1b.taskcare.service.infrastructure.persistence.jpa.repository.BoardEntityRepository;
import de.x1c1b.taskcare.service.infrastructure.persistence.jpa.repository.UserEntityRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
public class JpaBoardRepository implements BoardRepository {

    private final BoardEntityRepository boardEntityRepository;
    private final UserEntityRepository userEntityRepository;
    private final BoardEntityMapper boardEntityMapper;

    @Override
    public boolean hasMember(UUID id, String username) {
        return boardEntityRepository.existsByIdAndMembersUserUsername(id, username);
    }

    @Override
    public boolean hasMemberWithRole(UUID id, String username, Role role) {
        return boardEntityRepository.existsByIdAndMembersUserUsernameAndMembersRole(id, username, role.getName());
    }

    @Override
    public boolean hasMemberWithAnyRole(UUID id, String username, List<Role> roles) {
        return boardEntityRepository.existsByIdAndMembersUserUsernameAndMembersRoleIn(id, username,
                roles.stream().map(Role::getName).collect(Collectors.toList()));
    }

    @Override
    public Page<Board> findAllWithMembership(String username, PageSettings pageSettings) {
        var pageRequest = PageRequest.of(pageSettings.getPage(), pageSettings.getPerPage());
        return boardEntityMapper.mapToDomain(boardEntityRepository.findAllByMembersUserUsername(username, pageRequest));
    }

    @Override
    public Optional<Board> findById(UUID uuid) {
        return boardEntityRepository.findById(uuid)
                .map(boardEntityMapper::mapToDomain);
    }

    @Override
    public Page<Board> findAll(PageSettings pageSettings) {
        var pageRequest = PageRequest.of(pageSettings.getPage(), pageSettings.getPerPage());
        return boardEntityMapper.mapToDomain(boardEntityRepository.findAll(pageRequest));
    }

    @Override
    public boolean existsById(UUID uuid) {
        return boardEntityRepository.existsById(uuid);
    }

    @Override
    @Transactional
    public boolean deleteById(UUID uuid) {
        if (existsById(uuid)) {
            boardEntityRepository.deleteById(uuid);
            return true;
        }

        return false;
    }

    @Override
    @Transactional
    public void save(Board boardAggregate) {

        BoardEntity boardEntity = boardEntityMapper.mapToEntity(boardAggregate);
        boardEntityRepository.save(boardEntity);
    }
}
