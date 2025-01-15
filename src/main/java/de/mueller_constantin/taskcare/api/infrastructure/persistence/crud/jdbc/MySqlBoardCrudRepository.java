package de.mueller_constantin.taskcare.api.infrastructure.persistence.crud.jdbc;

import de.mueller_constantin.taskcare.api.core.common.domain.Page;
import de.mueller_constantin.taskcare.api.core.common.domain.PageInfo;
import de.mueller_constantin.taskcare.api.core.board.domain.BoardProjection;
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

@Component
@Transactional
@RequiredArgsConstructor
public class MySqlBoardCrudRepository implements BoardCrudRepository {
    private final String BOARD_TABLE_NAME = "boards";
    private final String MEMBER_TABLE_NAME = "members";
    private final String STATUS_TABLE_NAME = "statuses";

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public Optional<BoardProjection> findById(UUID id) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", id.toString());

        String query = """
            SELECT
                id,
                name,
                description
            FROM %s
            WHERE id = :id
        """.formatted(BOARD_TABLE_NAME);

        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(query, parameters, this::toBoardProjection));
        } catch (EmptyResultDataAccessException exc) {
            return Optional.empty();
        }
    }

    @Override
    public List<BoardProjection> findAll() {
        String query = """
            SELECT
                id,
                name,
                description
            FROM %s
        """.formatted(BOARD_TABLE_NAME);

        return jdbcTemplate.query(query, this::toBoardProjection);
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

        return jdbcTemplate.query(query, parameters, this::toBoardProjection);
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
        parameters.addValue("boardId", id.toString());

        query = """
            DELETE
            FROM %s
            WHERE board_id = :boardId
        """.formatted(STATUS_TABLE_NAME);

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
