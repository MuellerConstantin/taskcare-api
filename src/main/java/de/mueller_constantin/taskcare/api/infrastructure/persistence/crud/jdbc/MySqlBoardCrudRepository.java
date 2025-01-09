package de.mueller_constantin.taskcare.api.infrastructure.persistence.crud.jdbc;

import de.mueller_constantin.taskcare.api.core.common.domain.Page;
import de.mueller_constantin.taskcare.api.core.common.domain.PageInfo;
import de.mueller_constantin.taskcare.api.core.kanban.domain.BoardProjection;
import de.mueller_constantin.taskcare.api.core.kanban.domain.MemberProjection;
import de.mueller_constantin.taskcare.api.core.kanban.domain.Role;
import de.mueller_constantin.taskcare.api.infrastructure.persistence.crud.BoardCrudRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Transactional
@RequiredArgsConstructor
public class MySqlBoardCrudRepository implements BoardCrudRepository {
    private final String BOARD_TABLE_NAME = "boards";
    private final String MEMBER_TABLE_NAME = "members";

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public Optional<BoardProjection> findById(UUID id) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("boardId", id.toString());

        String query = """
            SELECT
                id,
                board_id,
                user_id,
                role
            FROM %s
            WHERE board_id = :boardId
        """.formatted(MEMBER_TABLE_NAME);

        Set<MemberProjection> memberProjections = new HashSet<>(jdbcTemplate.query(query, parameters, this::toMemberProjection));

        parameters = new MapSqlParameterSource();
        parameters.addValue("id", id.toString());

        query = """
            SELECT
                id,
                name,
                description
            FROM %s
            WHERE id = :id
        """.formatted(BOARD_TABLE_NAME);

        try {
            Optional<BoardProjection> boardProjection = Optional.ofNullable(
                    jdbcTemplate.queryForObject(query, parameters, this::toBoardProjection));

            return boardProjection.map(projection -> projection.toBuilder()
                    .members(memberProjections)
                    .build());
        } catch (EmptyResultDataAccessException exc) {
            return Optional.empty();
        }
    }

    @Override
    public List<BoardProjection> findAll() {
        String query = """
            SELECT
                id,
                board_id,
                user_id,
                role
            FROM %s
        """.formatted(MEMBER_TABLE_NAME);

        List<MemberProjection> memberProjections = jdbcTemplate.query(query, this::toMemberProjection);

        query = """
            SELECT
                id,
                name,
                description
            FROM %s
        """.formatted(BOARD_TABLE_NAME);

        List<BoardProjection> boardProjections = jdbcTemplate.query(query, this::toBoardProjection);

        return boardProjections.stream()
                .map(projection -> projection.toBuilder()
                        .members(memberProjections.stream()
                                .filter(m -> m.getBoardId().equals(projection.getId()))
                                .collect(Collectors.toSet()))
                        .build())
                .toList();
    }

    @Override
    public Page<BoardProjection> findAll(PageInfo pageInfo) {
        String query = """
            SELECT
                COUNT(*)
            FROM %s
        """.formatted(BOARD_TABLE_NAME);

        Integer totalElements = jdbcTemplate.queryForObject(query, new MapSqlParameterSource(), Integer.class);
        int totalPages = (int) Math.ceil((double) totalElements / pageInfo.getPerPage());

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("page", pageInfo.getPage());
        parameters.addValue("perPage", pageInfo.getPerPage());

        query = """
            SELECT
                id,
                name,
                description
            FROM %s
            LIMIT :perPage
            OFFSET :page
        """.formatted(BOARD_TABLE_NAME);

        List<BoardProjection> boardProjections = jdbcTemplate.query(query, parameters, this::toBoardProjection);

        parameters = new MapSqlParameterSource();
        parameters.addValue("boardIds", boardProjections.stream()
                .map(BoardProjection::getId)
                .map(UUID::toString)
                .toList());

        query = """
            SELECT
                id,
                board_id,
                user_id,
                role
            FROM %s
            WHERE board_id IN (:boardIds)
        """.formatted(MEMBER_TABLE_NAME);

        List<MemberProjection> memberProjections = jdbcTemplate.query(query, parameters, this::toMemberProjection);

        boardProjections = boardProjections.stream()
                .map(projection -> projection.toBuilder()
                        .members(memberProjections.stream()
                                .filter(m -> m.getBoardId().equals(projection.getId()))
                                .collect(Collectors.toSet()))
                        .build())
                .toList();

        return Page.<BoardProjection>builder()
                .content(boardProjections)
                .info(PageInfo.builder()
                        .page(pageInfo.getPage())
                        .perPage(pageInfo.getPerPage())
                        .totalElements(totalElements)
                        .totalPages(totalPages)
                        .build())
                .build();
    }

    @Override
    public Page<BoardProjection> findAllUserIsMember(UUID userId, PageInfo pageInfo) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("userId", userId.toString());

        String query = """
            SELECT
                COUNT(DISTINCT board_id)
            FROM %s
            WHERE user_id = :userId
        """.formatted(MEMBER_TABLE_NAME);

        Integer totalElements = jdbcTemplate.queryForObject(query, parameters, Integer.class);
        int totalPages = (int) Math.ceil((double) totalElements / pageInfo.getPerPage());

        parameters = new MapSqlParameterSource();
        parameters.addValue("userId", userId.toString());
        parameters.addValue("perPage", pageInfo.getPerPage());
        parameters.addValue("offset", pageInfo.getPage() * pageInfo.getPerPage());

        query = """
            SELECT
                DISTINCT board_id
            FROM %s
            WHERE user_id = :userId
            LIMIT :perPage
            OFFSET :offset
        """.formatted(MEMBER_TABLE_NAME);

        List<UUID> boardIds = jdbcTemplate.query(query, parameters, (rs, rowNum) -> UUID.fromString(rs.getString("board_id")));

        if (boardIds.isEmpty()) {
            return Page.<BoardProjection>builder()
                    .content(Collections.emptyList())
                    .info(PageInfo.builder()
                            .page(pageInfo.getPage())
                            .perPage(pageInfo.getPerPage())
                            .totalElements(totalElements)
                            .totalPages(totalPages)
                            .build())
                    .build();
        }

        parameters = new MapSqlParameterSource();
        parameters.addValue("boardIds", boardIds.stream()
                .map(UUID::toString)
                .toList());

        query = """
            SELECT
                id,
                name,
                description
            FROM %s
            WHERE id IN (:boardIds)
        """.formatted(BOARD_TABLE_NAME);

        List<BoardProjection> boardProjections = jdbcTemplate.query(query, parameters, this::toBoardProjection);

        parameters = new MapSqlParameterSource();
        parameters.addValue("boardIds", boardIds.stream()
                .map(UUID::toString)
                .toList());

        query = """
            SELECT
                id,
                board_id,
                user_id,
                role
            FROM %s
            WHERE board_id IN (:boardIds)
        """.formatted(MEMBER_TABLE_NAME);

        List<MemberProjection> memberProjections = jdbcTemplate.query(query, parameters, this::toMemberProjection);

        boardProjections = boardProjections.stream()
                .map(projection -> projection.toBuilder()
                        .members(memberProjections.stream()
                                .filter(m -> m.getBoardId().equals(projection.getId()))
                                .collect(Collectors.toSet()))
                        .build())
                .toList();

        return Page.<BoardProjection>builder()
                .content(boardProjections)
                .info(PageInfo.builder()
                        .page(pageInfo.getPage())
                        .perPage(pageInfo.getPerPage())
                        .totalElements(totalElements)
                        .totalPages(totalPages)
                        .build())
                .build();
    }

    @Override
    public List<BoardProjection> findAllUserIsMember(UUID userId) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("userId", userId.toString());

        String query = """
            SELECT
                DISTINCT board_id
            FROM %s
            WHERE user_id = :userId
        """.formatted(MEMBER_TABLE_NAME);

        List<UUID> boardIds = jdbcTemplate.query(query, parameters, (rs, rowNum) -> UUID.fromString(rs.getString("board_id")));

        if (boardIds.isEmpty()) {
            return List.of();
        }

        parameters = new MapSqlParameterSource();
        parameters.addValue("boardIds", boardIds.stream()
                .map(UUID::toString)
                .toList());

        query = """
            SELECT
                id,
                name,
                description
            FROM %s
            WHERE id IN (:boardIds)
        """.formatted(BOARD_TABLE_NAME);

        List<BoardProjection> boardProjections = jdbcTemplate.query(query, parameters, this::toBoardProjection);

        parameters = new MapSqlParameterSource();
        parameters.addValue("boardIds", boardIds.stream()
                .map(UUID::toString)
                .toList());

        query = """
            SELECT
                id,
                board_id,
                user_id,
                role
            FROM %s
            WHERE board_id IN (:boardIds)
        """.formatted(MEMBER_TABLE_NAME);

        List<MemberProjection> memberProjections = jdbcTemplate.query(query, parameters, this::toMemberProjection);

        return boardProjections.stream()
                .map(projection -> projection.toBuilder()
                        .members(memberProjections.stream()
                                .filter(m -> m.getBoardId().equals(projection.getId()))
                                .collect(Collectors.toSet()))
                        .build())
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("boardId", id.toString());

        String query = """
            DELETE
            FROM %s
            WHERE board_id = :boardId
        """.formatted(MEMBER_TABLE_NAME);

        jdbcTemplate.update(query, parameters);

        parameters = new MapSqlParameterSource();
        parameters.addValue("id", id.toString());

        query = """
            DELETE
            FROM %s
            WHERE id = :id
        """.formatted(BOARD_TABLE_NAME);

        jdbcTemplate.update(query, parameters);
    }

    @Override
    public void save(BoardProjection projection) {
        boolean exists = existsById(projection.getId());

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", projection.getId().toString());
        parameters.addValue("name", projection.getName());
        parameters.addValue("description", projection.getDescription());

        String query;

        if(exists) {
            query = """
                UPDATE %s
                SET
                    name = :name,
                    description = :description
                WHERE id = :id
            """.formatted(BOARD_TABLE_NAME);
        } else {
            query = """
                INSERT INTO %s (
                    id,
                    name,
                    description
                ) VALUES (
                    :id,
                    :name,
                    :description
                )
            """.formatted(BOARD_TABLE_NAME);
        }

        jdbcTemplate.update(query, parameters);

        // Delete removed members

        parameters = new MapSqlParameterSource();
        parameters.addValue("boardId", projection.getId().toString());
        parameters.addValue("memberIds", projection.getMembers().stream().map(MemberProjection::getId)
                .map(UUID::toString).collect(Collectors.toList()));

        query = """
            DELETE
            FROM %s
            WHERE board_id = :boardId AND id NOT IN (:memberIds)
        """.formatted(MEMBER_TABLE_NAME);

        jdbcTemplate.update(query, parameters);

        // Save current members

        parameters = new MapSqlParameterSource();
        parameters.addValue("boardId", projection.getId().toString());

        query = """
            SELECT
                id
            FROM %s
            WHERE board_id = :boardId
        """.formatted(MEMBER_TABLE_NAME);

        List<UUID> existingMemberIds = jdbcTemplate.query(query, parameters, (rs, rowNum) -> UUID.fromString(rs.getString("id")));
        List<UUID> newMemberIds = projection.getMembers().stream().map(MemberProjection::getId).filter(id -> !existingMemberIds.contains(id)).toList();

        if(!newMemberIds.isEmpty()) {
            List<MapSqlParameterSource> parametersList = projection.getMembers().stream()
                    .filter(m -> newMemberIds.contains(m.getId()))
                    .map(member -> {
                        MapSqlParameterSource nestedParameters = new MapSqlParameterSource();
                        nestedParameters.addValue("id", member.getId().toString());
                        nestedParameters.addValue("boardId", projection.getId().toString());
                        nestedParameters.addValue("userId", member.getUserId().toString());
                        nestedParameters.addValue("role", member.getRole().toString());
                        return nestedParameters;
                    })
                    .toList();

            query = """
                INSERT INTO %s (
                    id,
                    board_id,
                    user_id,
                    role
                ) VALUES (
                    :id,
                    :boardId,
                    :userId,
                    :role
                )
            """.formatted(MEMBER_TABLE_NAME);

            jdbcTemplate.batchUpdate(query, parametersList.toArray(new MapSqlParameterSource[0]));
        }

        if(!projection.getMembers().isEmpty()) {
            List<MapSqlParameterSource> parametersList = projection.getMembers().stream()
                    .filter(m -> existingMemberIds.contains(m.getId()))
                    .map(member -> {
                        MapSqlParameterSource nestedParameters = new MapSqlParameterSource();
                        nestedParameters.addValue("id", member.getId().toString());
                        nestedParameters.addValue("boardId", projection.getId().toString());
                        nestedParameters.addValue("userId", member.getUserId().toString());
                        nestedParameters.addValue("role", member.getRole().toString());
                        return nestedParameters;
                    })
                    .toList();

            query = """
                UPDATE %s
                SET
                    board_id = :boardId,
                    user_id = :userId,
                    role = :role
                WHERE id = :id
            """.formatted(MEMBER_TABLE_NAME);

            jdbcTemplate.batchUpdate(query, parametersList.toArray(new MapSqlParameterSource[0]));
        }
    }

    @Override
    public boolean existsById(UUID id) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", id.toString());

        String query = """
            SELECT
                COUNT(*)
            FROM %s
            WHERE id = :id
        """.formatted(BOARD_TABLE_NAME);

        Integer count = jdbcTemplate.queryForObject(query, parameters, Integer.class);

        return count != null && count > 0;
    }

    @SneakyThrows
    private MemberProjection toMemberProjection(ResultSet resultSet, int rowNum) {
        UUID id = UUID.fromString(resultSet.getString("id"));
        UUID boardId = UUID.fromString(resultSet.getString("board_id"));
        UUID userId = UUID.fromString(resultSet.getString("user_id"));
        Role role = Role.valueOf(resultSet.getString("role"));

        return MemberProjection.builder()
                .id(id)
                .boardId(boardId)
                .userId(userId)
                .role(role)
                .build();
    }

    @SneakyThrows
    private BoardProjection toBoardProjection(ResultSet resultSet, int rowNum) {
        UUID id = UUID.fromString(resultSet.getString("id"));
        String name = resultSet.getString("name");
        String description = resultSet.getString("description");

        return BoardProjection.builder()
                .id(id)
                .name(name)
                .description(description)
                .build();
    }
}
