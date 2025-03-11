package de.mueller_constantin.taskcare.api.infrastructure.persistence.crud.jdbc;

import de.mueller_constantin.taskcare.api.core.common.domain.Page;
import de.mueller_constantin.taskcare.api.core.common.domain.PageInfo;
import de.mueller_constantin.taskcare.api.core.task.domain.Priority;
import de.mueller_constantin.taskcare.api.core.task.domain.TaskProjection;
import de.mueller_constantin.taskcare.api.infrastructure.persistence.crud.TaskCrudRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.*;
import java.util.*;

@Component
@Transactional
@RequiredArgsConstructor
public class MySqlTaskCrudRepository implements TaskCrudRepository {
    private final String TASK_TABLE_NAME = "tasks";
    private final String TASK_COMPONENTS_TABLE_NAME = "task_components";

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public Optional<TaskProjection> findById(UUID id) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", id.toString());

        String query = """
            SELECT
                component_id
            FROM %s
            WHERE task_id = :id
        """.formatted(TASK_COMPONENTS_TABLE_NAME);

        List<UUID> componentIds = jdbcTemplate.query(query, parameters, (rs, rowNum) -> UUID.fromString(rs.getString("component_id")));

        query = """
            SELECT
                id,
                board_id,
                name,
                description,
                assignee_id,
                status_id,
                status_updated_at,
                due_date,
                created_at,
                updated_at,
                priority
            FROM %s
            WHERE id = :id
        """.formatted(TASK_TABLE_NAME);

        try {
            return Optional.ofNullable(
                            jdbcTemplate.queryForObject(query, parameters, this::toTaskProjection))
                    .map(b -> b.toBuilder().componentIds(new HashSet<>(componentIds)).build());
        } catch (EmptyResultDataAccessException exc) {
            return Optional.empty();
        }
    }

    @Override
    public List<TaskProjection> findAll() {
        String query = """
            SELECT
                id,
                board_id,
                name,
                description,
                assignee_id,
                status_id,
                status_updated_at,
                due_date,
                created_at,
                updated_at,
                priority
            FROM %s
        """.formatted(TASK_TABLE_NAME);

        List<TaskProjection> taskProjections = jdbcTemplate.query(query, this::toTaskProjection);

        if(taskProjections.isEmpty()) {
            return Collections.emptyList();
        }

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("taskIds", taskProjections.stream()
                .map(TaskProjection::getId).map(UUID::toString).toList());

        query = """
            SELECT
                task_id,
                component_id
            FROM %s
            WHERE task_id IN (:taskIds)
        """.formatted(TASK_COMPONENTS_TABLE_NAME);

        Map<UUID, Set<UUID>> componentIds = jdbcTemplate.query(query, parameters, rs -> {
            Map<UUID, Set<UUID>> component_id_mapping = new HashMap<>();

            while(rs.next()) {
                UUID taskId = UUID.fromString(rs.getString("task_id"));
                UUID componentId = UUID.fromString(rs.getString("component_id"));

                component_id_mapping.putIfAbsent(taskId, new HashSet<>());
                component_id_mapping.get(taskId).add(componentId);
            }

            return component_id_mapping;
        });

        return taskProjections.stream().map(b -> b.toBuilder()
                .componentIds(componentIds.getOrDefault(b.getId(), Collections.emptySet()))
                .build()).toList();
    }

    @Override
    public Page<TaskProjection> findAll(PageInfo pageInfo) {
        String query = """
            SELECT
                COUNT(*)
            FROM %s
        """.formatted(TASK_TABLE_NAME);

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
                description,
                assignee_id,
                status_id,
                status_updated_at,
                due_date,
                created_at,
                updated_at,
                priority
            FROM %s
            LIMIT :limit
            OFFSET :offset
        """.formatted(TASK_TABLE_NAME);

        List<TaskProjection> taskProjections = jdbcTemplate.query(query, parameters, this::toTaskProjection);

        if(taskProjections.isEmpty()) {
            return Page.<TaskProjection>builder()
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
        parameters.addValue("taskIds", taskProjections.stream()
                .map(TaskProjection::getId).map(UUID::toString).toList());

        query = """
            SELECT
                task_id,
                component_id
            FROM %s
            WHERE task_id IN (:taskIds)
        """.formatted(TASK_COMPONENTS_TABLE_NAME);

        Map<UUID, Set<UUID>> componentIds = jdbcTemplate.query(query, parameters, rs -> {
            Map<UUID, Set<UUID>> component_id_mapping = new HashMap<>();

            while(rs.next()) {
                UUID taskId = UUID.fromString(rs.getString("task_id"));
                UUID componentId = UUID.fromString(rs.getString("component_id"));

                component_id_mapping.putIfAbsent(taskId, new HashSet<>());
                component_id_mapping.get(taskId).add(componentId);
            }

            return component_id_mapping;
        });

        taskProjections = taskProjections.stream().map(b -> b.toBuilder()
                .componentIds(componentIds.getOrDefault(b.getId(), Collections.emptySet()))
                .build()).toList();

        return Page.<TaskProjection>builder()
                .content(taskProjections)
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
        parameters.addValue("taskId", id.toString());

        String query = """
            DELETE
            FROM %s
            WHERE task_id = :taskId
        """.formatted(TASK_COMPONENTS_TABLE_NAME);

        jdbcTemplate.update(query, parameters);

        parameters = new MapSqlParameterSource();
        parameters.addValue("id", id.toString());

        query = """
            DELETE
            FROM %s
            WHERE id = :id
        """.formatted(TASK_TABLE_NAME);

        jdbcTemplate.update(query, parameters);
    }

    @Override
    public void save(TaskProjection projection) {
        boolean exists = existsById(projection.getId());

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", projection.getId().toString());
        parameters.addValue("boardId", projection.getBoardId().toString());
        parameters.addValue("name", projection.getName());
        parameters.addValue("description", projection.getDescription());
        parameters.addValue("assigneeId", projection.getAssigneeId() == null ? null : projection.getAssigneeId().toString());
        parameters.addValue("statusId", projection.getStatusId() == null ? null : projection.getStatusId().toString());
        parameters.addValue("statusUpdatedAt", Timestamp.valueOf(LocalDateTime.ofInstant(projection.getStatusUpdatedAt().toInstant(), ZoneOffset.UTC)));
        parameters.addValue("dueDate", projection.getDueDate() != null ?
                Timestamp.valueOf(LocalDateTime.ofInstant(projection.getDueDate().toInstant(), ZoneOffset.UTC)) :
                null);
        parameters.addValue("createdAt", Timestamp.valueOf(LocalDateTime.ofInstant(projection.getCreatedAt().toInstant(), ZoneOffset.UTC)));
        parameters.addValue("updatedAt", Timestamp.valueOf(LocalDateTime.ofInstant(projection.getUpdatedAt().toInstant(), ZoneOffset.UTC)));
        parameters.addValue("priority", projection.getPriority() != null ? projection.getPriority().toString() : null);

        String query;

        if(exists) {
            query = """
                UPDATE %s
                SET
                    board_id = :boardId,
                    name = :name,
                    description = :description,
                    assignee_id = :assigneeId,
                    status_id = :statusId,
                    status_updated_at = :statusUpdatedAt,
                    due_date = :dueDate,
                    created_at = :createdAt,
                    updated_at = :updatedAt,
                    priority = :priority
                WHERE id = :id
            """.formatted(TASK_TABLE_NAME);
        } else {
            query = """
                INSERT INTO %s (
                    id,
                    board_id,
                    name,
                    description,
                    assignee_id,
                    status_id,
                    status_updated_at,
                    due_date,
                    created_at,
                    updated_at,
                    priority
                ) VALUES (
                    :id,
                    :boardId,
                    :name,
                    :description,
                    :assigneeId,
                    :statusId,
                    :statusUpdatedAt,
                    :dueDate,
                    :createdAt,
                    :updatedAt,
                    :priority
                )
            """.formatted(TASK_TABLE_NAME);
        }

        jdbcTemplate.update(query, parameters);

        parameters = new MapSqlParameterSource();
        parameters.addValue("taskId", projection.getId().toString());

        query = """
            DELETE
            FROM %s
            WHERE task_id = :taskId
        """.formatted(TASK_COMPONENTS_TABLE_NAME);

        jdbcTemplate.update(query, parameters);

        if(projection.getComponentIds() != null && !projection.getComponentIds().isEmpty()) {
            List<MapSqlParameterSource> parametersList = new ArrayList<>();
            List<UUID> componentIds = new ArrayList<>(projection.getComponentIds());

            for (UUID componentId : componentIds) {
                parameters = new MapSqlParameterSource();
                parameters.addValue("taskId", projection.getId().toString());
                parameters.addValue("componentId", componentId.toString());

                parametersList.add(parameters);
            }

            query = """
                INSERT INTO %s (task_id, component_id)
                VALUES (:taskId, :componentId)
            """.formatted(TASK_COMPONENTS_TABLE_NAME);

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
        """.formatted(TASK_TABLE_NAME);

        Integer count = jdbcTemplate.queryForObject(query, parameters, Integer.class);

        return count != null && count > 0;
    }

    @Override
    public Optional<TaskProjection> findByIdAndBoardId(UUID id, UUID boardId) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", id.toString());
        parameters.addValue("boardId", boardId.toString());

        String query = """
            SELECT
                component_id
            FROM %s
            WHERE task_id = :id AND board_id = :boardId
        """.formatted(TASK_COMPONENTS_TABLE_NAME);

        List<UUID> componentIds = jdbcTemplate.query(query, parameters, (rs, rowNum) -> UUID.fromString(rs.getString("component_id")));

        query = """
            SELECT
                id,
                board_id,
                name,
                description,
                assignee_id,
                status_id,
                status_updated_at,
                due_date,
                created_at,
                updated_at,
                priority
            FROM %s
            WHERE id = :id
        """.formatted(TASK_TABLE_NAME);

        try {
            return Optional.ofNullable(
                            jdbcTemplate.queryForObject(query, parameters, this::toTaskProjection))
                    .map(b -> b.toBuilder().componentIds(new HashSet<>(componentIds)).build());
        } catch (EmptyResultDataAccessException exc) {
            return Optional.empty();
        }
    }

    @Override
    public Page<TaskProjection> findAllByBoardId(UUID boardId, PageInfo pageInfo) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("boardId", boardId.toString());

        String query = """
            SELECT
                COUNT(*)
            FROM %s
            WHERE board_id = :boardId
        """.formatted(TASK_TABLE_NAME);

        Integer totalElements = jdbcTemplate.queryForObject(query, parameters, Integer.class);
        int totalPages = (int) Math.ceil((double) totalElements / pageInfo.getPerPage());

        parameters = new MapSqlParameterSource();
        parameters.addValue("offset", pageInfo.getPage() * pageInfo.getPerPage());
        parameters.addValue("limit", pageInfo.getPerPage());
        parameters.addValue("boardId", boardId.toString());

        query = """
            SELECT
                id,
                board_id,
                name,
                description,
                assignee_id,
                status_id,
                status_updated_at,
                due_date,
                created_at,
                updated_at,
                priority
            FROM %s
            WHERE board_id = :boardId
            LIMIT :limit
            OFFSET :offset
        """.formatted(TASK_TABLE_NAME);

        List<TaskProjection> taskProjections = jdbcTemplate.query(query, parameters, this::toTaskProjection);

        if(taskProjections.isEmpty()) {
            return Page.<TaskProjection>builder()
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
        parameters.addValue("taskIds", taskProjections.stream()
                .map(TaskProjection::getId).map(UUID::toString).toList());

        query = """
            SELECT
                task_id,
                component_id
            FROM %s
            WHERE task_id IN (:taskIds)
        """.formatted(TASK_COMPONENTS_TABLE_NAME);

        Map<UUID, Set<UUID>> componentIds = jdbcTemplate.query(query, parameters, rs -> {
            Map<UUID, Set<UUID>> component_id_mapping = new HashMap<>();

            while(rs.next()) {
                UUID taskId = UUID.fromString(rs.getString("task_id"));
                UUID componentId = UUID.fromString(rs.getString("component_id"));

                component_id_mapping.putIfAbsent(taskId, new HashSet<>());
                component_id_mapping.get(taskId).add(componentId);
            }

            return component_id_mapping;
        });

        taskProjections = taskProjections.stream().map(b -> b.toBuilder()
                .componentIds(componentIds.getOrDefault(b.getId(), Collections.emptySet()))
                .build()).toList();

        return Page.<TaskProjection>builder()
                .content(taskProjections)
                .info(PageInfo.builder()
                        .page(pageInfo.getPage())
                        .perPage(pageInfo.getPerPage())
                        .totalElements(totalElements)
                        .totalPages(totalPages)
                        .build())
                .build();
    }

    @Override
    public Page<TaskProjection> findAllByBoardIdAndNoStatus(UUID boardId, PageInfo pageInfo) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("boardId", boardId.toString());

        String query = """
            SELECT
                COUNT(*)
            FROM %s
            WHERE board_id = :boardId AND status_id IS NULL
        """.formatted(TASK_TABLE_NAME);

        Integer totalElements = jdbcTemplate.queryForObject(query, parameters, Integer.class);
        int totalPages = (int) Math.ceil((double) totalElements / pageInfo.getPerPage());

        parameters = new MapSqlParameterSource();
        parameters.addValue("offset", pageInfo.getPage() * pageInfo.getPerPage());
        parameters.addValue("limit", pageInfo.getPerPage());
        parameters.addValue("boardId", boardId.toString());

        query = """
            SELECT
                id,
                board_id,
                name,
                description,
                assignee_id,
                status_id,
                status_updated_at,
                due_date,
                created_at,
                updated_at,
                priority
            FROM %s
            WHERE board_id = :boardId AND status_id IS NULL
            LIMIT :limit
            OFFSET :offset
        """.formatted(TASK_TABLE_NAME);

        List<TaskProjection> taskProjections = jdbcTemplate.query(query, parameters, this::toTaskProjection);

        if(taskProjections.isEmpty()) {
            return Page.<TaskProjection>builder()
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
        parameters.addValue("taskIds", taskProjections.stream()
                .map(TaskProjection::getId).map(UUID::toString).toList());

        query = """
            SELECT
                task_id,
                component_id
            FROM %s
            WHERE task_id IN (:taskIds)
        """.formatted(TASK_COMPONENTS_TABLE_NAME);

        Map<UUID, Set<UUID>> componentIds = jdbcTemplate.query(query, parameters, rs -> {
            Map<UUID, Set<UUID>> component_id_mapping = new HashMap<>();

            while(rs.next()) {
                UUID taskId = UUID.fromString(rs.getString("task_id"));
                UUID componentId = UUID.fromString(rs.getString("component_id"));

                component_id_mapping.putIfAbsent(taskId, new HashSet<>());
                component_id_mapping.get(taskId).add(componentId);
            }

            return component_id_mapping;
        });

        taskProjections = taskProjections.stream().map(b -> b.toBuilder()
                .componentIds(componentIds.getOrDefault(b.getId(), Collections.emptySet()))
                .build()).toList();

        return Page.<TaskProjection>builder()
                .content(taskProjections)
                .info(PageInfo.builder()
                        .page(pageInfo.getPage())
                        .perPage(pageInfo.getPerPage())
                        .totalElements(totalElements)
                        .totalPages(totalPages)
                        .build())
                .build();
    }

    @Override
    public Page<TaskProjection> findAllByBoardIdAndStatusId(UUID boardId, UUID statusId, PageInfo pageInfo) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("boardId", boardId.toString());
        parameters.addValue("statusId", statusId.toString());

        String query = """
            SELECT
                COUNT(*)
            FROM %s
            WHERE board_id = :boardId AND status_id = :statusId
        """.formatted(TASK_TABLE_NAME);

        Integer totalElements = jdbcTemplate.queryForObject(query, parameters, Integer.class);
        int totalPages = (int) Math.ceil((double) totalElements / pageInfo.getPerPage());

        parameters = new MapSqlParameterSource();
        parameters.addValue("offset", pageInfo.getPage() * pageInfo.getPerPage());
        parameters.addValue("limit", pageInfo.getPerPage());
        parameters.addValue("boardId", boardId.toString());
        parameters.addValue("statusId", statusId.toString());

        query = """
            SELECT
                id,
                board_id,
                name,
                description,
                assignee_id,
                status_id,
                status_updated_at,
                due_date,
                created_at,
                updated_at,
                priority
            FROM %s
            WHERE board_id = :boardId AND status_id = :statusId
            LIMIT :limit
            OFFSET :offset
        """.formatted(TASK_TABLE_NAME);

        List<TaskProjection> taskProjections = jdbcTemplate.query(query, parameters, this::toTaskProjection);

        if(taskProjections.isEmpty()) {
            return Page.<TaskProjection>builder()
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
        parameters.addValue("taskIds", taskProjections.stream()
                .map(TaskProjection::getId).map(UUID::toString).toList());

        query = """
            SELECT
                task_id,
                component_id
            FROM %s
            WHERE task_id IN (:taskIds)
        """.formatted(TASK_COMPONENTS_TABLE_NAME);

        Map<UUID, Set<UUID>> componentIds = jdbcTemplate.query(query, parameters, rs -> {
            Map<UUID, Set<UUID>> component_id_mapping = new HashMap<>();

            while(rs.next()) {
                UUID taskId = UUID.fromString(rs.getString("task_id"));
                UUID componentId = UUID.fromString(rs.getString("component_id"));

                component_id_mapping.putIfAbsent(taskId, new HashSet<>());
                component_id_mapping.get(taskId).add(componentId);
            }

            return component_id_mapping;
        });

        taskProjections = taskProjections.stream().map(b -> b.toBuilder()
                .componentIds(componentIds.getOrDefault(b.getId(), Collections.emptySet()))
                .build()).toList();

        return Page.<TaskProjection>builder()
                .content(taskProjections)
                .info(PageInfo.builder()
                        .page(pageInfo.getPage())
                        .perPage(pageInfo.getPerPage())
                        .totalElements(totalElements)
                        .totalPages(totalPages)
                        .build())
                .build();
    }

    @Override
    public Page<TaskProjection> findAllByBoardIdAndComponentId(UUID boardId, UUID componentId, PageInfo pageInfo) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("boardId", boardId.toString());
        parameters.addValue("componentId", componentId.toString());

        String query = """
            SELECT
                COUNT(*)
            FROM %s
            WHERE board_id = :boardId AND component_id = :componentId
        """.formatted(TASK_TABLE_NAME);

        Integer totalElements = jdbcTemplate.queryForObject(query, parameters, Integer.class);
        int totalPages = (int) Math.ceil((double) totalElements / pageInfo.getPerPage());

        parameters = new MapSqlParameterSource();
        parameters.addValue("offset", pageInfo.getPage() * pageInfo.getPerPage());
        parameters.addValue("limit", pageInfo.getPerPage());
        parameters.addValue("boardId", boardId.toString());
        parameters.addValue("componentId", componentId.toString());

        query = """
            SELECT
                id,
                board_id,
                name,
                description,
                assignee_id,
                status_id,
                status_updated_at,
                due_date,
                created_at,
                updated_at,
                priority
            FROM %s
            WHERE board_id = :boardId AND component_id = :componentId
            LIMIT :limit
            OFFSET :offset
        """.formatted(TASK_TABLE_NAME);

        List<TaskProjection> taskProjections = jdbcTemplate.query(query, parameters, this::toTaskProjection);

        if(taskProjections.isEmpty()) {
            return Page.<TaskProjection>builder()
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
        parameters.addValue("taskIds", taskProjections.stream()
                .map(TaskProjection::getId).map(UUID::toString).toList());

        query = """
            SELECT
                task_id,
                component_id
            FROM %s
            WHERE task_id IN (:taskIds)
        """.formatted(TASK_COMPONENTS_TABLE_NAME);

        Map<UUID, Set<UUID>> componentIds = jdbcTemplate.query(query, parameters, rs -> {
            Map<UUID, Set<UUID>> component_id_mapping = new HashMap<>();

            while(rs.next()) {
                UUID taskId = UUID.fromString(rs.getString("task_id"));
                UUID componentId1 = UUID.fromString(rs.getString("component_id"));

                component_id_mapping.putIfAbsent(taskId, new HashSet<>());
                component_id_mapping.get(taskId).add(componentId1);
            }

            return component_id_mapping;
        });

        taskProjections = taskProjections.stream().map(b -> b.toBuilder()
                .componentIds(componentIds.getOrDefault(b.getId(), Collections.emptySet()))
                .build()).toList();

        return Page.<TaskProjection>builder()
                .content(taskProjections)
                .info(PageInfo.builder()
                        .page(pageInfo.getPage())
                        .perPage(pageInfo.getPerPage())
                        .totalElements(totalElements)
                        .totalPages(totalPages)
                        .build())
                .build();
    }

    @Override
    public Page<TaskProjection> findAllByBoardIdAndAssigneeId(UUID boardId, UUID assigneeId, PageInfo pageInfo) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("boardId", boardId.toString());
        parameters.addValue("assigneeId", assigneeId.toString());

        String query = """
            SELECT
                COUNT(*)
            FROM %s
            WHERE board_id = :boardId AND assignee_id = :assigneeId
        """.formatted(TASK_TABLE_NAME);

        Integer totalElements = jdbcTemplate.queryForObject(query, parameters, Integer.class);
        int totalPages = (int) Math.ceil((double) totalElements / pageInfo.getPerPage());

        parameters = new MapSqlParameterSource();
        parameters.addValue("offset", pageInfo.getPage() * pageInfo.getPerPage());
        parameters.addValue("limit", pageInfo.getPerPage());
        parameters.addValue("boardId", boardId.toString());
        parameters.addValue("assigneeId", assigneeId.toString());

        query = """
            SELECT
                id,
                board_id,
                name,
                description,
                assignee_id,
                status_id,
                status_updated_at,
                due_date,
                created_at,
                updated_at,
                priority
            FROM %s
            WHERE board_id = :boardId AND assignee_id = :assigneeId
            LIMIT :limit
            OFFSET :offset
        """.formatted(TASK_TABLE_NAME);

        List<TaskProjection> taskProjections = jdbcTemplate.query(query, parameters, this::toTaskProjection);

        if(taskProjections.isEmpty()) {
            return Page.<TaskProjection>builder()
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
        parameters.addValue("taskIds", taskProjections.stream()
                .map(TaskProjection::getId).map(UUID::toString).toList());

        query = """
            SELECT
                task_id,
                component_id
            FROM %s
            WHERE task_id IN (:taskIds)
        """.formatted(TASK_COMPONENTS_TABLE_NAME);

        Map<UUID, Set<UUID>> componentIds = jdbcTemplate.query(query, parameters, rs -> {
            Map<UUID, Set<UUID>> component_id_mapping = new HashMap<>();

            while(rs.next()) {
                UUID taskId = UUID.fromString(rs.getString("task_id"));
                UUID componentId = UUID.fromString(rs.getString("component_id"));

                component_id_mapping.putIfAbsent(taskId, new HashSet<>());
                component_id_mapping.get(taskId).add(componentId);
            }

            return component_id_mapping;
        });

        taskProjections = taskProjections.stream().map(b -> b.toBuilder()
                .componentIds(componentIds.getOrDefault(b.getId(), Collections.emptySet()))
                .build()).toList();

        return Page.<TaskProjection>builder()
                .content(taskProjections)
                .info(PageInfo.builder()
                        .page(pageInfo.getPage())
                        .perPage(pageInfo.getPerPage())
                        .totalElements(totalElements)
                        .totalPages(totalPages)
                        .build())
                .build();
    }

    @Override
    public List<UUID> findAllIdsByBoardId(UUID boardId) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("boardId", boardId.toString());

        String query = """
            SELECT
                id
            FROM %s
            WHERE board_id = :boardId
        """.formatted(TASK_TABLE_NAME);

        return jdbcTemplate.query(query, parameters, rs -> {
            List<UUID> ids = new ArrayList<>();
            while(rs.next()) {
                ids.add(UUID.fromString(rs.getString("id")));
            }
            return ids;
        });
    }

    @Override
    public List<UUID> findAllIdsByBoardIdAndStatusId(UUID boardId, UUID statusId) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("boardId", boardId.toString());
        parameters.addValue("statusId", statusId.toString());

        String query = """
            SELECT
                id
            FROM %s
            WHERE board_id = :boardId AND status_id = :statusId
        """.formatted(TASK_TABLE_NAME);

        return jdbcTemplate.query(query, parameters, rs -> {
            List<UUID> ids = new ArrayList<>();
            while(rs.next()) {
                ids.add(UUID.fromString(rs.getString("id")));
            }
            return ids;
        });
    }

    @Override
    public List<UUID> findAllIdsByBoardIdAndComponentId(UUID boardId, UUID componentId) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("boardId", boardId.toString());
        parameters.addValue("componentId", componentId.toString());

        String query = """
            SELECT
                id
            FROM %s
            WHERE board_id = :boardId AND id IN (
                SELECT
                    task_id
                FROM %s
                WHERE component_id = :componentId
            )
        """.formatted(TASK_TABLE_NAME, TASK_COMPONENTS_TABLE_NAME);

        return jdbcTemplate.query(query, parameters, rs -> {
            List<UUID> ids = new ArrayList<>();
            while(rs.next()) {
                ids.add(UUID.fromString(rs.getString("id")));
            }
            return ids;
        });
    }

    @Override
    public List<UUID> findAllIdsByBoardIdAndAssigneeId(UUID boardId, UUID assigneeId) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("boardId", boardId.toString());
        parameters.addValue("assigneeId", assigneeId.toString());

        String query = """
            SELECT
                id
            FROM %s
            WHERE board_id = :boardId AND assignee_id = :assigneeId
        """.formatted(TASK_TABLE_NAME);

        return jdbcTemplate.query(query, parameters, rs -> {
            List<UUID> ids = new ArrayList<>();
            while(rs.next()) {
                ids.add(UUID.fromString(rs.getString("id")));
            }
            return ids;
        });
    }

    @SneakyThrows
    private TaskProjection toTaskProjection(ResultSet resultSet, int rowNum) {
        UUID id = UUID.fromString(resultSet.getString("id"));
        UUID boardId = UUID.fromString(resultSet.getString("board_id"));
        String name = resultSet.getString("name");
        String description = resultSet.getString("description");

        UUID assigneeId = resultSet.getString("assignee_id") != null ?
                UUID.fromString(resultSet.getString("assignee_id")) :
                null;

        UUID statusId = resultSet.getString("status_id") != null ?
                UUID.fromString(resultSet.getString("status_id")) :
                null;

        Timestamp statusUpdatedAtTimestamp = resultSet.getTimestamp("status_updated_at");
        OffsetDateTime statusUpdatedAt;

        if(statusUpdatedAtTimestamp == null) {
            statusUpdatedAt = null;
        } else {
            LocalDateTime localStatusUpdatedAt = statusUpdatedAtTimestamp.toLocalDateTime();
            statusUpdatedAt = localStatusUpdatedAt.atOffset(ZoneOffset.UTC);
        }

        Timestamp dueDateTimestamp = resultSet.getTimestamp("due_date");
        OffsetDateTime dueDate;

        if(dueDateTimestamp == null) {
            dueDate = null;
        } else {
            LocalDateTime localDueDate = dueDateTimestamp.toLocalDateTime();
            dueDate = localDueDate.atOffset(ZoneOffset.UTC);
        }

        LocalDateTime localCreatedAt = resultSet.getTimestamp("created_at").toLocalDateTime();
        OffsetDateTime createdAt = localCreatedAt.atOffset(ZoneOffset.UTC);

        Priority priority = resultSet.getString("priority") != null ?
                Priority.valueOf(resultSet.getString("priority")) :
                null;

        LocalDateTime localUpdatedAt = resultSet.getTimestamp("created_at").toLocalDateTime();
        OffsetDateTime updatedAt = localUpdatedAt.atOffset(ZoneOffset.UTC);

        return TaskProjection.builder()
                .id(id)
                .boardId(boardId)
                .name(name)
                .description(description)
                .assigneeId(assigneeId)
                .statusId(statusId)
                .statusUpdatedAt(statusUpdatedAt)
                .dueDate(dueDate)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .priority(priority)
                .build();
    }
}
