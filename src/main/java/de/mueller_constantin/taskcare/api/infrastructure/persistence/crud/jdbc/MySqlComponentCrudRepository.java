package de.mueller_constantin.taskcare.api.infrastructure.persistence.crud.jdbc;

import de.mueller_constantin.taskcare.api.core.board.domain.ComponentProjection;
import de.mueller_constantin.taskcare.api.core.common.domain.Page;
import de.mueller_constantin.taskcare.api.core.common.domain.PageInfo;
import de.mueller_constantin.taskcare.api.infrastructure.persistence.crud.ComponentCrudRepository;
import de.mueller_constantin.taskcare.api.infrastructure.persistence.crud.jdbc.rsql.MySqlComponentRSQLConverter;
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
public class MySqlComponentCrudRepository implements ComponentCrudRepository {
    private final String COMPONENT_TABLE_NAME = "components";

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public Optional<ComponentProjection> findById(UUID id) {
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
        """.formatted(COMPONENT_TABLE_NAME);

        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(query, parameters, this::toComponentProjection));
        } catch (EmptyResultDataAccessException exc) {
            return Optional.empty();
        }
    }

    @Override
    public List<ComponentProjection> findAll() {
        String query = """
            SELECT
                id,
                board_id,
                name,
                description
            FROM %s
        """.formatted(COMPONENT_TABLE_NAME);

        return jdbcTemplate.query(query, this::toComponentProjection);
    }

    @Override
    public Page<ComponentProjection> findAll(PageInfo pageInfo) {
        String query = """
            SELECT
                COUNT(*)
            FROM %s
        """.formatted(COMPONENT_TABLE_NAME);

        Integer totalElements = jdbcTemplate.queryForObject(query, new MapSqlParameterSource(), Integer.class);
        int totalPages = (int) Math.ceil((double) totalElements / pageInfo.getPerPage());

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("offset", pageInfo.getPage() * pageInfo.getPerPage());
        parameters.addValue("limit", pageInfo.getPerPage());

        query = """
            SELECT
                id,
                board_id,
                name,
                description
            FROM %s
            LIMIT :limit
            OFFSET :offset
        """.formatted(COMPONENT_TABLE_NAME);

        List<ComponentProjection> componentProjections = jdbcTemplate.query(query, parameters, this::toComponentProjection);

        return Page.<ComponentProjection>builder()
                .content(componentProjections)
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
        """.formatted(COMPONENT_TABLE_NAME);

        jdbcTemplate.update(query, parameters);
    }

    @Override
    public void save(ComponentProjection projection) {
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
            """.formatted(COMPONENT_TABLE_NAME);
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
            """.formatted(COMPONENT_TABLE_NAME);
        }

        jdbcTemplate.update(query, parameters);
    }

    @Override
    public void saveAllForBoardId(UUID boardId, List<ComponentProjection> projections) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("boardId", boardId.toString());

        String query = """
            SELECT
                id
            FROM %s
            WHERE board_id = :boardId
        """.formatted(COMPONENT_TABLE_NAME);

        List<UUID> existingComponentIds = jdbcTemplate.query(query, parameters, (rs, rowNum) -> UUID.fromString(rs.getString("id")));
        List<UUID> newComponentIds = projections.stream().map(ComponentProjection::getId).filter(id -> !existingComponentIds.contains(id)).toList();

        if(!newComponentIds.isEmpty()) {
            List<MapSqlParameterSource> parametersList = projections.stream()
                    .filter(c -> newComponentIds.contains(c.getId()))
                    .map(component -> {
                        MapSqlParameterSource nestedParameters = new MapSqlParameterSource();
                        nestedParameters.addValue("id", component.getId().toString());
                        nestedParameters.addValue("boardId", boardId.toString());
                        nestedParameters.addValue("name", component.getName());
                        nestedParameters.addValue("description", component.getDescription());
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
                    """.formatted(COMPONENT_TABLE_NAME);

            jdbcTemplate.batchUpdate(query, parametersList.toArray(new MapSqlParameterSource[0]));
        }

        if(!projections.isEmpty()) {
            List<MapSqlParameterSource> parametersList = projections.stream()
                    .filter(c -> existingComponentIds.contains(c.getId()))
                    .map(component -> {
                        MapSqlParameterSource nestedParameters = new MapSqlParameterSource();
                        nestedParameters.addValue("id", component.getId().toString());
                        nestedParameters.addValue("boardId", boardId.toString());
                        nestedParameters.addValue("name", component.getName());
                        nestedParameters.addValue("description", component.getDescription());
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
            """.formatted(COMPONENT_TABLE_NAME);

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
        """.formatted(COMPONENT_TABLE_NAME);

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
        """.formatted(COMPONENT_TABLE_NAME);

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
        """.formatted(COMPONENT_TABLE_NAME);

        Integer count = jdbcTemplate.queryForObject(query, parameters, Integer.class);

        return count != null && count > 0;
    }

    @Override
    public Optional<ComponentProjection> findByIdAndBoardId(UUID id, UUID boardId) {
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
        """.formatted(COMPONENT_TABLE_NAME);

        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(query, parameters, this::toComponentProjection));
        } catch (EmptyResultDataAccessException exc) {
            return Optional.empty();
        }
    }

    @Override
    public Page<ComponentProjection> findAllByBoardId(UUID boarId, PageInfo pageInfo) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("boardId", boarId.toString());

        String query = """
            SELECT
                COUNT(*)
            FROM %s
            WHERE board_id = :boardId
        """.formatted(COMPONENT_TABLE_NAME);

        Integer count = jdbcTemplate.queryForObject(query, parameters, Integer.class);
        int totalPages = (int) Math.ceil((double) count / pageInfo.getPerPage());

        parameters = new MapSqlParameterSource();
        parameters.addValue("boardId", boarId.toString());
        parameters.addValue("limit", pageInfo.getPerPage());
        parameters.addValue("offset", pageInfo.getPage() * pageInfo.getPerPage());

        query = """
            SELECT
                id,
                board_id,
                name,
                description
            FROM %s
            WHERE board_id = :boardId
            LIMIT :limit
            OFFSET :offset
        """.formatted(COMPONENT_TABLE_NAME);

        List<ComponentProjection> componentProjections = jdbcTemplate.query(query, parameters, this::toComponentProjection);

        return Page.<ComponentProjection>builder()
                .content(componentProjections)
                .info(PageInfo.builder()
                        .page(pageInfo.getPage())
                        .perPage(pageInfo.getPerPage())
                        .totalElements(count)
                        .totalPages(totalPages)
                        .build())
                .build();
    }

    @Override
    public Page<ComponentProjection> findAllByBoardId(UUID boarId, PageInfo pageInfo, String predicate) {
        if(predicate == null) {
            return findAllByBoardId(boarId, pageInfo);
        }

        MySqlComponentRSQLConverter converter = new MySqlComponentRSQLConverter();
        converter.parse(predicate);

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("boardId", boarId.toString());
        parameters.addValues(converter.getParameters().getValues());

        String query = """
            SELECT
                COUNT(*)
            FROM %s
            WHERE board_id = :boardId AND %s
        """.formatted(COMPONENT_TABLE_NAME, converter.getQuery());

        Integer count = jdbcTemplate.queryForObject(query, parameters, Integer.class);
        int totalPages = (int) Math.ceil((double) count / pageInfo.getPerPage());

        parameters = new MapSqlParameterSource();
        parameters.addValue("boardId", boarId.toString());
        parameters.addValue("limit", pageInfo.getPerPage());
        parameters.addValue("offset", pageInfo.getPage() * pageInfo.getPerPage());
        parameters.addValues(converter.getParameters().getValues());

        query = """
            SELECT
                id,
                board_id,
                name,
                description
            FROM %s
            WHERE board_id = :boardId AND %s
            LIMIT :limit
            OFFSET :offset
        """.formatted(COMPONENT_TABLE_NAME, converter.getQuery());

        List<ComponentProjection> componentProjections = jdbcTemplate.query(query, parameters, this::toComponentProjection);

        return Page.<ComponentProjection>builder()
                .content(componentProjections)
                .info(PageInfo.builder()
                        .page(pageInfo.getPage())
                        .perPage(pageInfo.getPerPage())
                        .totalElements(count)
                        .totalPages(totalPages)
                        .build())
                .build();
    }

    @SneakyThrows
    private ComponentProjection toComponentProjection(ResultSet resultSet, int rowNum) {
        UUID id = UUID.fromString(resultSet.getString("id"));
        UUID boardId = UUID.fromString(resultSet.getString("board_id"));
        String name = resultSet.getString("name");
        String description = resultSet.getString("description");

        return ComponentProjection.builder()
                .id(id)
                .boardId(boardId)
                .name(name)
                .description(description)
                .build();
    }
}
