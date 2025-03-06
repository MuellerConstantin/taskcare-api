package de.mueller_constantin.taskcare.api.infrastructure.persistence.crud.jdbc;

import de.mueller_constantin.taskcare.api.core.board.domain.ColumnProjection;
import de.mueller_constantin.taskcare.api.core.common.domain.Page;
import de.mueller_constantin.taskcare.api.core.common.domain.PageInfo;
import de.mueller_constantin.taskcare.api.core.board.domain.BoardProjection;
import de.mueller_constantin.taskcare.api.infrastructure.persistence.crud.BoardCrudRepository;
import de.mueller_constantin.taskcare.api.infrastructure.persistence.crud.jdbc.rsql.MySqlBoardRSQLConverter;
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
    private final String COMPONENT_TABLE_NAME = "components";
    private final String BOARD_COLUMNS_TABLE_NAME = "board_columns";

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public Optional<BoardProjection> findById(UUID id) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", id.toString());

        String query = """
            SELECT
                status_id
            FROM %s
            WHERE board_id = :id
            ORDER BY position
        """.formatted(BOARD_COLUMNS_TABLE_NAME);

        List<ColumnProjection> columns = jdbcTemplate.query(query, parameters, this::toColumnProjection);

        query = """
            SELECT
                id,
                name,
                description
            FROM %s
            WHERE id = :id
        """.formatted(BOARD_TABLE_NAME);

        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(query, parameters, this::toBoardProjection))
                    .map(b -> b.toBuilder().columns(columns).build());
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

        List<BoardProjection> boardProjections = jdbcTemplate.query(query, this::toBoardProjection);

        if(boardProjections.isEmpty()) {
            return Collections.emptyList();
        }

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("boardIds", boardProjections.stream()
                .map(BoardProjection::getId).map(UUID::toString).toList());

        query = """
            SELECT
                status_id,
                board_id
            FROM %s
            WHERE board_id IN (:boardIds)
            ORDER BY status_id, position
        """.formatted(BOARD_COLUMNS_TABLE_NAME);

        Map<UUID, List<ColumnProjection>> columns = jdbcTemplate.query(query, parameters, rs -> {
            Map<UUID, List<ColumnProjection>> column_mapping = new HashMap<>();

            while(rs.next()) {
                UUID boardId = UUID.fromString(rs.getString("board_id"));
                ColumnProjection columnProjection = toColumnProjection(rs, 1);

                column_mapping.putIfAbsent(boardId, new ArrayList<>());
                column_mapping.get(boardId).add(columnProjection);
            }

            return column_mapping;
        });

        return boardProjections.stream().map(b -> b.toBuilder()
                .columns(columns.getOrDefault(b.getId(), Collections.emptyList()))
                .build()).toList();
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
        parameters.addValue("offset", pageInfo.getPage() * pageInfo.getPerPage());
        parameters.addValue("limit", pageInfo.getPerPage());

        query = """
            SELECT
                id,
                name,
                description
            FROM %s
            LIMIT :limit
            OFFSET :offset
        """.formatted(BOARD_TABLE_NAME);

        List<BoardProjection> boardProjections = jdbcTemplate.query(query, parameters, this::toBoardProjection);

        if(boardProjections.isEmpty()) {
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
        parameters.addValue("boardIds", boardProjections.stream()
                .map(BoardProjection::getId).map(UUID::toString).toList());

        query = """
            SELECT
                status_id,
                board_id
            FROM %s
            WHERE board_id IN (:boardIds)
            ORDER BY status_id, position
        """.formatted(BOARD_COLUMNS_TABLE_NAME);

        Map<UUID, List<ColumnProjection>> columns = jdbcTemplate.query(query, parameters, rs -> {
            Map<UUID, List<ColumnProjection>> column_mapping = new HashMap<>();

            while(rs.next()) {
                UUID boardId = UUID.fromString(rs.getString("board_id"));
                ColumnProjection columnProjection = toColumnProjection(rs, 1);

                column_mapping.putIfAbsent(boardId, new ArrayList<>());
                column_mapping.get(boardId).add(columnProjection);
            }

            return column_mapping;
        });

        boardProjections = boardProjections.stream().map(b -> b.toBuilder()
                .columns(columns.getOrDefault(b.getId(), Collections.emptyList()))
                .build()).toList();

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
    public Page<BoardProjection> findAll(PageInfo pageInfo, String predicate) {
        if(predicate == null) {
            return findAll(pageInfo);
        }

        MySqlBoardRSQLConverter converter = new MySqlBoardRSQLConverter();
        converter.parse(predicate);

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValues(converter.getParameters().getValues());

        String query = """
            SELECT
                COUNT(*)
            FROM %s
            WHERE %s
        """.formatted(BOARD_TABLE_NAME, converter.getQuery());

        Integer totalElements = jdbcTemplate.queryForObject(query, parameters, Integer.class);
        int totalPages = (int) Math.ceil((double) totalElements / pageInfo.getPerPage());

        parameters = new MapSqlParameterSource();
        parameters.addValue("offset", pageInfo.getPage() * pageInfo.getPerPage());
        parameters.addValue("limit", pageInfo.getPerPage());
        parameters.addValues(converter.getParameters().getValues());

        query = """
            SELECT
                id,
                name,
                description
            FROM %s
            WHERE %s
            LIMIT :limit
            OFFSET :offset
        """.formatted(BOARD_TABLE_NAME, converter.getQuery());

        List<BoardProjection> boardProjections = jdbcTemplate.query(query, parameters, this::toBoardProjection);

        if(boardProjections.isEmpty()) {
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
        parameters.addValue("boardIds", boardProjections.stream()
                .map(BoardProjection::getId).map(UUID::toString).toList());

        query = """
            SELECT
                status_id,
                board_id
            FROM %s
            WHERE board_id IN (:boardIds)
            ORDER BY status_id, position
        """.formatted(BOARD_COLUMNS_TABLE_NAME);

        Map<UUID, List<ColumnProjection>> columns = jdbcTemplate.query(query, parameters, rs -> {
            Map<UUID, List<ColumnProjection>> column_mapping = new HashMap<>();

            while(rs.next()) {
                UUID boardId = UUID.fromString(rs.getString("board_id"));
                ColumnProjection columnProjection = toColumnProjection(rs, 1);

                column_mapping.putIfAbsent(boardId, new ArrayList<>());
                column_mapping.get(boardId).add(columnProjection);
            }

            return column_mapping;
        });

        boardProjections = boardProjections.stream().map(b -> b.toBuilder()
                .columns(columns.getOrDefault(b.getId(), Collections.emptyList()))
                .build()).toList();

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
        parameters.addValue("limit", pageInfo.getPerPage());
        parameters.addValue("offset", pageInfo.getPage() * pageInfo.getPerPage());

        query = """
            SELECT
                DISTINCT board_id
            FROM %s
            WHERE user_id = :userId
            LIMIT :limit
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

        if(boardProjections.isEmpty()) {
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
        parameters.addValue("boardIds", boardProjections.stream()
                .map(BoardProjection::getId).map(UUID::toString).toList());

        query = """
            SELECT
                status_id,
                board_id
            FROM %s
            WHERE board_id IN (:boardIds)
            ORDER BY status_id, position
        """.formatted(BOARD_COLUMNS_TABLE_NAME);

        Map<UUID, List<ColumnProjection>> columns = jdbcTemplate.query(query, parameters, rs -> {
            Map<UUID, List<ColumnProjection>> column_mapping = new HashMap<>();

            while(rs.next()) {
                UUID boardId = UUID.fromString(rs.getString("board_id"));
                ColumnProjection columnProjection = toColumnProjection(rs, 1);

                column_mapping.putIfAbsent(boardId, new ArrayList<>());
                column_mapping.get(boardId).add(columnProjection);
            }

            return column_mapping;
        });

        boardProjections = boardProjections.stream().map(b -> b.toBuilder()
                .columns(columns.getOrDefault(b.getId(), Collections.emptyList()))
                .build()).toList();

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
    public Page<BoardProjection> findAllUserIsMember(UUID userId, PageInfo pageInfo, String predicate) {
        if(predicate == null) {
            return findAllUserIsMember(userId, pageInfo);
        }

        MySqlBoardRSQLConverter converter = new MySqlBoardRSQLConverter();
        converter.parse(predicate);

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("userId", userId.toString());
        parameters.addValues(converter.getParameters().getValues());

        String query = """
            SELECT
                COUNT(DISTINCT id)
            FROM %s
            WHERE id IN (
                SELECT board_id
                FROM %s
                WHERE user_id = :userId
            ) AND %s
        """.formatted(BOARD_TABLE_NAME, MEMBER_TABLE_NAME, converter.getQuery());

        Integer totalElements = jdbcTemplate.queryForObject(query, parameters, Integer.class);
        int totalPages = (int) Math.ceil((double) totalElements / pageInfo.getPerPage());

        parameters = new MapSqlParameterSource();
        parameters.addValue("userId", userId.toString());
        parameters.addValue("limit", pageInfo.getPerPage());
        parameters.addValue("offset", pageInfo.getPage() * pageInfo.getPerPage());
        parameters.addValues(converter.getParameters().getValues());

        query = """
            SELECT
                DISTINCT id
            FROM %s
            WHERE id IN (
                SELECT board_id
                FROM %s
                WHERE user_id = :userId
            ) AND %s
            LIMIT :limit
            OFFSET :offset
        """.formatted(BOARD_TABLE_NAME, MEMBER_TABLE_NAME, converter.getQuery());

        List<UUID> boardIds = jdbcTemplate.query(query, parameters, (rs, rowNum) -> UUID.fromString(rs.getString("id")));

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

        if(boardProjections.isEmpty()) {
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
        parameters.addValue("boardIds", boardProjections.stream()
                .map(BoardProjection::getId).map(UUID::toString).toList());

        query = """
            SELECT
                status_id,
                board_id
            FROM %s
            WHERE board_id IN (:boardIds)
            ORDER BY status_id, position
        """.formatted(BOARD_COLUMNS_TABLE_NAME);

        Map<UUID, List<ColumnProjection>> columns = jdbcTemplate.query(query, parameters, rs -> {
            Map<UUID, List<ColumnProjection>> column_mapping = new HashMap<>();

            while(rs.next()) {
                UUID boardId = UUID.fromString(rs.getString("board_id"));
                ColumnProjection columnProjection = toColumnProjection(rs, 1);

                column_mapping.putIfAbsent(boardId, new ArrayList<>());
                column_mapping.get(boardId).add(columnProjection);
            }

            return column_mapping;
        });

        boardProjections = boardProjections.stream().map(b -> b.toBuilder()
                .columns(columns.getOrDefault(b.getId(), Collections.emptyList()))
                .build()).toList();

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

        if(boardProjections.isEmpty()) {
            return Collections.emptyList();
        }

        parameters = new MapSqlParameterSource();
        parameters.addValue("boardIds", boardProjections.stream()
                .map(BoardProjection::getId).map(UUID::toString).toList());

        query = """
            SELECT
                status_id,
                board_id
            FROM %s
            WHERE board_id IN (:boardIds)
            ORDER BY status_id, position
        """.formatted(BOARD_COLUMNS_TABLE_NAME);

        Map<UUID, List<ColumnProjection>> columns = jdbcTemplate.query(query, parameters, rs -> {
            Map<UUID, List<ColumnProjection>> column_mapping = new HashMap<>();

            while(rs.next()) {
                UUID boardId = UUID.fromString(rs.getString("board_id"));
                ColumnProjection columnProjection = toColumnProjection(rs, 1);

                column_mapping.putIfAbsent(boardId, new ArrayList<>());
                column_mapping.get(boardId).add(columnProjection);
            }

            return column_mapping;
        });

        return boardProjections.stream().map(b -> b.toBuilder()
                .columns(columns.getOrDefault(b.getId(), Collections.emptyList()))
                .build()).toList();
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
        """.formatted(BOARD_COLUMNS_TABLE_NAME);

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
        parameters.addValue("boardId", id.toString());

        query = """
            DELETE
            FROM %s
            WHERE board_id = :boardId
        """.formatted(COMPONENT_TABLE_NAME);

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

        parameters = new MapSqlParameterSource();
        parameters.addValue("boardId", projection.getId().toString());

        query = """
            DELETE
            FROM %s
            WHERE board_id = :boardId
        """.formatted(BOARD_COLUMNS_TABLE_NAME);

        jdbcTemplate.update(query, parameters);

        if(projection.getColumns() != null && !projection.getColumns().isEmpty()) {
            List<MapSqlParameterSource> parametersList = new ArrayList<>();

            for(int index = 0; index < projection.getColumns().size(); index++) {
                ColumnProjection column = projection.getColumns().get(index);
                MapSqlParameterSource nestedParameters = new MapSqlParameterSource();

                nestedParameters.addValue("status_id", column.getStatusId().toString());
                nestedParameters.addValue("board_id", projection.getId().toString());
                nestedParameters.addValue("position", index);

                parametersList.add(nestedParameters);
            }

            query = """
                INSERT INTO %s (
                    status_id,
                    board_id,
                    position
                ) VALUES (
                    :status_id,
                    :board_id,
                    :position
                )
            """.formatted(BOARD_COLUMNS_TABLE_NAME);

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

    @SneakyThrows
    private ColumnProjection toColumnProjection(ResultSet resultSet, int rowNum) {
        UUID statusId = UUID.fromString(resultSet.getString("status_id"));

        return ColumnProjection.builder()
                .statusId(statusId)
                .build();
    }
}
