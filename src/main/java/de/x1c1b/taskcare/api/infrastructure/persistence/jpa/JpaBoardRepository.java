package de.x1c1b.taskcare.api.infrastructure.persistence.jpa;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import de.x1c1b.taskcare.api.core.board.domain.Board;
import de.x1c1b.taskcare.api.core.board.domain.BoardRepository;
import de.x1c1b.taskcare.api.core.board.domain.Role;
import de.x1c1b.taskcare.api.core.common.domain.FilterSettings;
import de.x1c1b.taskcare.api.core.common.domain.Page;
import de.x1c1b.taskcare.api.core.common.domain.PageSettings;
import de.x1c1b.taskcare.api.infrastructure.persistence.jpa.entity.BoardEntity;
import de.x1c1b.taskcare.api.infrastructure.persistence.jpa.entity.mapper.BoardEntityMapper;
import de.x1c1b.taskcare.api.infrastructure.persistence.jpa.repository.BoardEntityRepository;
import de.x1c1b.taskcare.api.infrastructure.persistence.jpa.repository.UserEntityRepository;
import de.x1c1b.taskcare.api.infrastructure.persistence.jpa.rsql.JpaRSQLVisitor;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
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
    public Page<Board> findAllWithMembership(String username, FilterSettings filterSettings, PageSettings pageSettings) {
        var pageRequest = PageRequest.of(pageSettings.getPage(), pageSettings.getPerPage());

        if (null != filterSettings.getFilter()) {
            Node rootNode = new RSQLParser().parse(filterSettings.getFilter());
            Specification<BoardEntity> specification = rootNode.accept(new JpaRSQLVisitor<>());

            return boardEntityMapper.mapToDomain(boardEntityRepository.findAllByMembersUserUsername(username, specification, pageRequest));
        } else {
            return boardEntityMapper.mapToDomain(boardEntityRepository.findAllByMembersUserUsername(username, pageRequest));
        }
    }

    @Override
    public Optional<Board> findById(UUID uuid) {
        return boardEntityRepository.findById(uuid)
                .map(boardEntityMapper::mapToDomain);
    }

    @Override
    public Page<Board> findAll(FilterSettings filterSettings, PageSettings pageSettings) {
        var pageRequest = PageRequest.of(pageSettings.getPage(), pageSettings.getPerPage());

        if (null != filterSettings.getFilter()) {
            Node rootNode = new RSQLParser().parse(filterSettings.getFilter());
            Specification<BoardEntity> specification = rootNode.accept(new JpaRSQLVisitor<>());

            return boardEntityMapper.mapToDomain(boardEntityRepository.findAll(specification, pageRequest));
        } else {
            return boardEntityMapper.mapToDomain(boardEntityRepository.findAll(pageRequest));
        }
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
