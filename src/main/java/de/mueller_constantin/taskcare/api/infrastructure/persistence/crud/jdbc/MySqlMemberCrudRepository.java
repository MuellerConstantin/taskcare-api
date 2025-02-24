package de.mueller_constantin.taskcare.api.infrastructure.persistence.crud.jdbc;

import de.mueller_constantin.taskcare.api.core.common.domain.Page;
import de.mueller_constantin.taskcare.api.core.common.domain.PageInfo;
import de.mueller_constantin.taskcare.api.core.board.domain.MemberProjection;
import de.mueller_constantin.taskcare.api.core.board.domain.Role;
import de.mueller_constantin.taskcare.api.infrastructure.persistence.crud.MemberCrudRepository;
import de.mueller_constantin.taskcare.api.infrastructure.persistence.crud.jdbc.rsql.MySqlMemberRSQLConverter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@Transactional
@RequiredArgsConstructor
public class MySqlMemberCrudRepository implements MemberCrudRepository {
    private final String MEMBER_TABLE_NAME = "members";

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public Optional<MemberProjection> findByIdAndBoardId(UUID id, UUID boardId) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", id.toString());
        parameters.addValue("boardId", boardId.toString());

        String query = """
            SELECT
                id,
                board_id,
                user_id,
                role
            FROM %s
            WHERE id = :id AND board_id = :boardId
        """.formatted(MEMBER_TABLE_NAME);

        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(query, parameters, this::toMemberProjection));
        } catch (EmptyResultDataAccessException exc) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<MemberProjection> findByUserIdAndBoardId(UUID userId, UUID boardId) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("userId", userId.toString());
        parameters.addValue("boardId", boardId.toString());

        String query = """
            SELECT
                id,
                board_id,
                user_id,
                role
            FROM %s
            WHERE user_id = :userId AND board_id = :boardId
        """.formatted(MEMBER_TABLE_NAME);

        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(query, parameters, this::toMemberProjection));
        } catch (EmptyResultDataAccessException exc) {
            return Optional.empty();
        }
    }

    @Override
    public Page<MemberProjection> findAllByBoardId(UUID boarId, PageInfo pageInfo) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("boardId", boarId.toString());

        String query = """
            SELECT
                COUNT(*)
            FROM %s
            WHERE board_id = :boardId
        """.formatted(MEMBER_TABLE_NAME);

        Integer totalElements = jdbcTemplate.queryForObject(query, parameters, Integer.class);
        int totalPages = (int) Math.ceil((double) totalElements / pageInfo.getPerPage());

        parameters = new MapSqlParameterSource();
        parameters.addValue("boardId", boarId.toString());
        parameters.addValue("limit", pageInfo.getPerPage());
        parameters.addValue("offset", pageInfo.getPage() * pageInfo.getPerPage());

        query = """
            SELECT
                id,
                board_id,
                user_id,
                role
            FROM %s
            WHERE board_id = :boardId
            LIMIT :limit
            OFFSET :offset
        """.formatted(MEMBER_TABLE_NAME);

        List<MemberProjection> memberProjections = jdbcTemplate.query(query, parameters, this::toMemberProjection);

        return Page.<MemberProjection>builder()
                .content(memberProjections)
                .info(PageInfo.builder()
                        .page(pageInfo.getPage())
                        .perPage(pageInfo.getPerPage())
                        .totalElements(totalElements)
                        .totalPages(totalPages)
                        .build())
                .build();
    }

    @Override
    public Page<MemberProjection> findAllByBoardId(UUID boarId, PageInfo pageInfo, String predicate) {
        if(predicate == null) {
            return findAllByBoardId(boarId, pageInfo);
        }

        MySqlMemberRSQLConverter converter = new MySqlMemberRSQLConverter();
        converter.parse(predicate);

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("boardId", boarId.toString());
        parameters.addValues(converter.getParameters().getValues());

        String query = """
            SELECT
                COUNT(*)
            FROM %s
            WHERE board_id = :boardId AND %s
        """.formatted(MEMBER_TABLE_NAME, converter.getQuery());

        Integer totalElements = jdbcTemplate.queryForObject(query, parameters, Integer.class);
        int totalPages = (int) Math.ceil((double) totalElements / pageInfo.getPerPage());

        parameters = new MapSqlParameterSource();
        parameters.addValue("boardId", boarId.toString());
        parameters.addValue("limit", pageInfo.getPerPage());
        parameters.addValue("offset", pageInfo.getPage() * pageInfo.getPerPage());
        parameters.addValues(converter.getParameters().getValues());

        query = """
            SELECT
                id,
                board_id,
                user_id,
                role
            FROM %s
            WHERE board_id = :boardId AND %s
            LIMIT :limit
            OFFSET :offset
        """.formatted(MEMBER_TABLE_NAME, converter.getQuery());

        List<MemberProjection> memberProjections = jdbcTemplate.query(query, parameters, this::toMemberProjection);

        return Page.<MemberProjection>builder()
                .content(memberProjections)
                .info(PageInfo.builder()
                        .page(pageInfo.getPage())
                        .perPage(pageInfo.getPerPage())
                        .totalElements(totalElements)
                        .totalPages(totalPages)
                        .build())
                .build();
    }

    @Override
    public boolean existsByUserIdAndBoardId(UUID userId, UUID boardId) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("userId", userId.toString());
        parameters.addValue("boardId", boardId.toString());

        String query = """
            SELECT
                COUNT(*)
            FROM %s
            WHERE user_id = :userId AND board_id = :boardId
        """.formatted(MEMBER_TABLE_NAME);

        Integer count = jdbcTemplate.queryForObject(query, parameters, Integer.class);

        return count != null && count > 0;
    }

    @Override
    public boolean existsByIdAndBoardId(UUID id, UUID boardId) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", id.toString());
        parameters.addValue("boardId", boardId.toString());

        String query = """
            SELECT
                COUNT(*)
            FROM %s
            WHERE id = :id AND board_id = :boardId
        """.formatted(MEMBER_TABLE_NAME);

        Integer count = jdbcTemplate.queryForObject(query, parameters, Integer.class);

        return count != null && count > 0;
    }

    @Override
    public Optional<MemberProjection> findById(UUID id) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", id.toString());

        String query = """
            SELECT
                id,
                board_id,
                user_id,
                role
            FROM %s
            WHERE id = :id
        """.formatted(MEMBER_TABLE_NAME);

        try {
            return  Optional.ofNullable(
                    jdbcTemplate.queryForObject(query, parameters, this::toMemberProjection));
        } catch (EmptyResultDataAccessException exc) {
            return Optional.empty();
        }
    }

    @Override
    public List<MemberProjection> findAll() {
        String query = """
            SELECT
                id,
                board_id,
                user_id,
                role
            FROM %s
        """.formatted(MEMBER_TABLE_NAME);

        return jdbcTemplate.query(query, this::toMemberProjection);
    }

    @Override
    public Page<MemberProjection> findAll(PageInfo pageInfo) {
        String query = """
            SELECT
                COUNT(*)
            FROM %s
        """.formatted(MEMBER_TABLE_NAME);

        Integer totalElements = jdbcTemplate.queryForObject(query, new MapSqlParameterSource(), Integer.class);
        int totalPages = (int) Math.ceil((double) totalElements / pageInfo.getPerPage());

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("limit", pageInfo.getPerPage());
        parameters.addValue("offset", pageInfo.getPage() * pageInfo.getPerPage());

        query = """
            SELECT
                id,
                board_id,
                user_id,
                role
            FROM %s
            LIMIT :limit
            OFFSET :offset
        """.formatted(MEMBER_TABLE_NAME);

        List<MemberProjection> memberProjections = jdbcTemplate.query(query, parameters, this::toMemberProjection);

        return Page.<MemberProjection>builder()
                .content(memberProjections)
                .info(PageInfo.builder()
                        .page(pageInfo.getPage())
                        .perPage(pageInfo.getPerPage())
                        .totalElements(totalElements)
                        .totalPages(totalPages)
                        .build())
                .build();
    }

    @Override
    public void deleteById(UUID id) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", id.toString());

        String query = """
            DELETE
            FROM %s
            WHERE id = :id
        """.formatted(MEMBER_TABLE_NAME);

        jdbcTemplate.update(query, parameters);
    }

    @Override
    public void save(MemberProjection projection) {
        boolean exists = existsById(projection.getId());

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", projection.getId().toString());
        parameters.addValue("boardId", projection.getBoardId().toString());
        parameters.addValue("userId", projection.getUserId().toString());
        parameters.addValue("role", projection.getRole().toString());

        String query;

        if(exists) {
            query = """
                UPDATE %s
                SET
                    board_id = :boardId,
                    user_id = :userId,
                    role = :role
                WHERE id = :id
            """.formatted(MEMBER_TABLE_NAME);
        } else {
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
        }

        jdbcTemplate.update(query, parameters);
    }

    @Override
    public void saveAllForBoardId(UUID boardId, List<MemberProjection> projections) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("boardId", boardId.toString());

        String query = """
            SELECT
                id
            FROM %s
            WHERE board_id = :boardId
        """.formatted(MEMBER_TABLE_NAME);

        List<UUID> existingMemberIds = jdbcTemplate.query(query, parameters, (rs, rowNum) -> UUID.fromString(rs.getString("id")));
        List<UUID> newMemberIds = projections.stream().map(MemberProjection::getId).filter(id -> !existingMemberIds.contains(id)).toList();

        if(!newMemberIds.isEmpty()) {
            List<MapSqlParameterSource> parametersList = projections.stream()
                    .filter(m -> newMemberIds.contains(m.getId()))
                    .map(member -> {
                        MapSqlParameterSource nestedParameters = new MapSqlParameterSource();
                        nestedParameters.addValue("id", member.getId().toString());
                        nestedParameters.addValue("boardId", boardId.toString());
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

        if(!projections.isEmpty()) {
            List<MapSqlParameterSource> parametersList = projections.stream()
                    .filter(m -> existingMemberIds.contains(m.getId()))
                    .map(member -> {
                        MapSqlParameterSource nestedParameters = new MapSqlParameterSource();
                        nestedParameters.addValue("id", member.getId().toString());
                        nestedParameters.addValue("boardId", boardId.toString());
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
        """.formatted(MEMBER_TABLE_NAME);

        Integer count = jdbcTemplate.queryForObject(query, parameters, Integer.class);

        return count != null && count > 0;
    }

    @Override
    public void deleteAllByBoardId(UUID boardId) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("boardId", boardId.toString());

        String query = """
            DELETE
            FROM %s
            WHERE board_id = :boardId
        """.formatted(MEMBER_TABLE_NAME);

        jdbcTemplate.update(query, parameters);
    }

    @Override
    public void deleteAllNotInIdsForBoardId(List<UUID> ids, UUID boardId) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("boardId", boardId.toString());

        if(ids.isEmpty()) {
            parameters.addValue("ids", List.of("-1"));
        } else {
            parameters.addValue("ids", ids.stream()
                    .map(UUID::toString)
                    .toList());
        }

        String query = """
            DELETE
            FROM %s
            WHERE board_id = :boardId AND id NOT IN (:ids)
        """.formatted(MEMBER_TABLE_NAME);

        jdbcTemplate.update(query, parameters);
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
}
