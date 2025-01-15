package de.mueller_constantin.taskcare.api.infrastructure.persistence.crud.jdbc;

import de.mueller_constantin.taskcare.api.core.common.domain.Page;
import de.mueller_constantin.taskcare.api.core.common.domain.PageInfo;
import de.mueller_constantin.taskcare.api.core.board.domain.StatusProjection;
import de.mueller_constantin.taskcare.api.infrastructure.persistence.crud.StatusCrudRepository;
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
public class MySqlStatusCrudRepository implements StatusCrudRepository {
    private final String STATUS_TABLE_NAME = "statuses";

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public Optional<StatusProjection> findById(UUID id) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", id.toString());

        String query = """
            SELECT
                id,
                board_id,
                name,
                description
            FROM %s
            WHERE id = :id
        """.formatted(STATUS_TABLE_NAME);

        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(query, parameters, this::toStatusProjection));
        } catch (EmptyResultDataAccessException exc) {
            return Optional.empty();
        }
    }

    @Override
    public List<StatusProjection> findAll() {
        String query = """
            SELECT
                id,
                board_id,
                name,
                description
            FROM %s
        """.formatted(STATUS_TABLE_NAME);

        return jdbcTemplate.query(query, this::toStatusProjection);
    }

    @Override
    public Page<StatusProjection> findAll(PageInfo pageInfo) {
        String query = """
            SELECT
                COUNT(*)
            FROM %s
        """.formatted(STATUS_TABLE_NAME);

        Integer totalElements = jdbcTemplate.queryForObject(query, new MapSqlParameterSource(), Integer.class);
        int totalPages = (int) Math.ceil((double) totalElements / pageInfo.getPerPage());

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("page", pageInfo.getPage());
        parameters.addValue("perPage", pageInfo.getPerPage());

        query = """
            SELECT
                id,
                board_id,
                name,
                description
            FROM %s
            LIMIT :perPage
            OFFSET :page
        """.formatted(STATUS_TABLE_NAME);

        List<StatusProjection> statusProjections = jdbcTemplate.query(query, parameters, this::toStatusProjection);

        return Page.<StatusProjection>builder()
                .content(statusProjections)
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
        """.formatted(STATUS_TABLE_NAME);

        jdbcTemplate.update(query, parameters);
    }

    @Override
    public void save(StatusProjection projection) {
        boolean exists = existsById(projection.getId());

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", projection.getId().toString());
        parameters.addValue("boardId", projection.getBoardId().toString());
        parameters.addValue("name", projection.getName());
        parameters.addValue("description", projection.getDescription());

        String query;

        if(exists) {
            query = """
                UPDATE %s
                SET
                    board_id = :boardId,
                    name = :name,
                    description = :description
                WHERE id = :id
            """.formatted(STATUS_TABLE_NAME);
        } else {
            query = """
                INSERT INTO %s (
                    id,
                    board_id,
                    name,
                    description
                ) VALUES (
                    :id,
                    :boardId,
                    :name,
                    :description
                )
            """.formatted(STATUS_TABLE_NAME);
        }

        jdbcTemplate.update(query, parameters);
    }

    @Override
    public void saveAllForBoardId(UUID boardId, List<StatusProjection> projections) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("boardId", boardId.toString());

        String query = """
            SELECT
                id
            FROM %s
            WHERE board_id = :boardId
        """.formatted(STATUS_TABLE_NAME);

        List<UUID> existingStatusIds = jdbcTemplate.query(query, parameters, (rs, rowNum) -> UUID.fromString(rs.getString("id")));
        List<UUID> newStatusIds = projections.stream().map(StatusProjection::getId).filter(id -> !existingStatusIds.contains(id)).toList();

        if(!newStatusIds.isEmpty()) {
            List<MapSqlParameterSource> parametersList = projections.stream()
                    .filter(s -> newStatusIds.contains(s.getId()))
                    .map(status -> {
                        MapSqlParameterSource nestedParameters = new MapSqlParameterSource();
                        nestedParameters.addValue("id", status.getId().toString());
                        nestedParameters.addValue("boardId", boardId.toString());
                        nestedParameters.addValue("name", status.getName());
                        nestedParameters.addValue("description", status.getDescription());
                        return nestedParameters;
                    })
                    .toList();

            query = """
                        INSERT INTO %s (
                            id,
                            board_id,
                            name,
                            description
                        ) VALUES (
                            :id,
                            :boardId,
                            :name,
                            :description
                        )
                    """.formatted(STATUS_TABLE_NAME);

            jdbcTemplate.batchUpdate(query, parametersList.toArray(new MapSqlParameterSource[0]));
        }

        if(!projections.isEmpty()) {
            List<MapSqlParameterSource> parametersList = projections.stream()
                    .filter(s -> existingStatusIds.contains(s.getId()))
                    .map(status -> {
                        MapSqlParameterSource nestedParameters = new MapSqlParameterSource();
                        nestedParameters.addValue("id", status.getId().toString());
                        nestedParameters.addValue("boardId", boardId.toString());
                        nestedParameters.addValue("name", status.getName());
                        nestedParameters.addValue("description", status.getDescription());
                        return nestedParameters;
                    })
                    .toList();

            query = """
                UPDATE %s
                SET
                    board_id = :boardId,
                    name = :name,
                    description = :description
                WHERE id = :id
            """.formatted(STATUS_TABLE_NAME);

            jdbcTemplate.batchUpdate(query, parametersList.toArray(new MapSqlParameterSource[0]));
        }
    }

    @Override
    public void deleteAllByBoardId(UUID boardId) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("boardId", boardId.toString());

        String query = """
            DELETE
            FROM %s
            WHERE board_id = :boardId
        """.formatted(STATUS_TABLE_NAME);

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
        """.formatted(STATUS_TABLE_NAME);

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
        """.formatted(STATUS_TABLE_NAME);

        Integer count = jdbcTemplate.queryForObject(query, parameters, Integer.class);

        return count != null && count > 0;
    }

    @Override
    public Optional<StatusProjection> findByIdAndBoardId(UUID id, UUID boardId) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", id.toString());
        parameters.addValue("boardId", boardId.toString());

        String query = """
            SELECT
                id,
                board_id,
                name,
                description
            FROM %s
            WHERE id = :id AND board_id = :boardId
        """.formatted(STATUS_TABLE_NAME);

        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(query, parameters, this::toStatusProjection));
        } catch (EmptyResultDataAccessException exc) {
            return Optional.empty();
        }
    }

    @Override
    public Page<StatusProjection> findAllByBoardId(UUID boarId, PageInfo pageInfo) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("boardId", boarId.toString());

        String query = """
            SELECT
                COUNT(*)
            FROM %s
            WHERE board_id = :boardId
        """.formatted(STATUS_TABLE_NAME);

        Integer count = jdbcTemplate.queryForObject(query, parameters, Integer.class);
        int totalPages = (int) Math.ceil((double) count / pageInfo.getPerPage());

        parameters = new MapSqlParameterSource();
        parameters.addValue("boardId", boarId.toString());
        parameters.addValue("perPage", pageInfo.getPerPage());
        parameters.addValue("offset", pageInfo.getPage() * pageInfo.getPerPage());

        query = """
            SELECT
                id,
                board_id,
                name,
                description
            FROM %s
            WHERE board_id = :boardId
            LIMIT :perPage
            OFFSET :offset
        """.formatted(STATUS_TABLE_NAME);

        List<StatusProjection> statusProjections = jdbcTemplate.query(query, parameters, this::toStatusProjection);

        return Page.<StatusProjection>builder()
                .content(statusProjections)
                .info(PageInfo.builder()
                        .page(pageInfo.getPage())
                        .perPage(pageInfo.getPerPage())
                        .totalElements(count)
                        .totalPages(totalPages)
                        .build())
                .build();
    }

    @SneakyThrows
    private StatusProjection toStatusProjection(ResultSet resultSet, int rowNum) {
        UUID id = UUID.fromString(resultSet.getString("id"));
        UUID boardId = UUID.fromString(resultSet.getString("board_id"));
        String name = resultSet.getString("name");
        String description = resultSet.getString("description");

        return StatusProjection.builder()
                .id(id)
                .boardId(boardId)
                .name(name)
                .description(description)
                .build();
    }
}
