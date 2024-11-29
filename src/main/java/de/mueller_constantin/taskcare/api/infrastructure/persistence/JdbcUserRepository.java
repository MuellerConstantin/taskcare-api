package de.mueller_constantin.taskcare.api.infrastructure.persistence;

import de.mueller_constantin.taskcare.api.core.common.domain.model.Page;
import de.mueller_constantin.taskcare.api.core.common.domain.model.PageInfo;
import de.mueller_constantin.taskcare.api.core.user.domain.model.Role;
import de.mueller_constantin.taskcare.api.core.user.domain.model.UserAggregate;
import de.mueller_constantin.taskcare.api.core.user.domain.model.UserProjection;
import de.mueller_constantin.taskcare.api.core.user.application.repository.UserAggregateRepository;
import de.mueller_constantin.taskcare.api.core.user.application.repository.UserProjectionRepository;
import de.mueller_constantin.taskcare.api.infrastructure.persistence.es.EventStore;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.Types;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@Transactional
@RequiredArgsConstructor
public class JdbcUserRepository implements UserAggregateRepository, UserProjectionRepository {
    private final String USER_TABLE_NAME = "users";

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final EventStore eventStore;

    @Override
    public void save(UserAggregate aggregate) {
        eventStore.saveAggregate(aggregate);

        // Synchronize read model with event store

        if(aggregate.isDeleted()) {
            MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue("id", aggregate.getId().toString());

            jdbcTemplate.update("""
                DELETE FROM %s
                WHERE id = :id
                """.formatted(USER_TABLE_NAME), parameters);
        } else {
            MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue("id", aggregate.getId().toString());
            parameters.addValue("username", aggregate.getUsername());
            parameters.addValue("password", aggregate.getPassword());
            parameters.addValue("displayName", aggregate.getDisplayName());
            parameters.addValue("role", aggregate.getRole().toString());
            parameters.addValue("locked", aggregate.isLocked(), Types.BOOLEAN);

            jdbcTemplate.update("""
                INSERT INTO %s (id, username, password, display_name, role, locked)
                VALUES (:id, :username, :password, :displayName, :role, :locked)
                ON DUPLICATE KEY UPDATE username = :username, password = :password, display_name = :displayName, role = :role, locked = :locked
                """.formatted(USER_TABLE_NAME), parameters);
        }
    }

    @Override
    public Optional<UserAggregate> load(UUID aggregateId) {
        return eventStore.loadAggregate(aggregateId, UserAggregate.class, null);
    }

    @Override
    public Optional<UserAggregate> load(UUID aggregateId, Integer version) {
        return eventStore.loadAggregate(aggregateId, UserAggregate.class, version);
    }

    @Override
    public Optional<UserProjection> findByUsername(String username) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("username", username);

        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("""
                SELECT id, username, password, display_name, role, locked
                FROM %s
                WHERE username = :username
                """.formatted(USER_TABLE_NAME), parameters, this::toProjection));
        } catch (EmptyResultDataAccessException exc) {
            return Optional.empty();
        }
    }

    @Override
    public boolean existsByUsername(String username) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("username", username);

        Integer count = jdbcTemplate.queryForObject("""
                SELECT COUNT(*)
                FROM %s
                WHERE username = :username
                """.formatted(USER_TABLE_NAME), parameters, Integer.class);

        return count != null && count > 0;
    }

    @Override
    public Optional<UserProjection> findById(UUID id) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", id.toString());

        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("""
                SELECT id, username, password, display_name, role, locked
                FROM %s
                WHERE id = :id
                """.formatted(USER_TABLE_NAME), parameters, this::toProjection));
        } catch (EmptyResultDataAccessException exc) {
            return Optional.empty();
        }
    }

    @Override
    public List<UserProjection> findAll() {
        return jdbcTemplate.query("""
                SELECT id, username, password, display_name, role, locked
                FROM %s
                """.formatted(USER_TABLE_NAME), this::toProjection);
    }

    @Override
    public Page<UserProjection> findAll(PageInfo pageInfo) {
        Integer totalElements = jdbcTemplate.queryForObject("""
                SELECT COUNT(*)
                FROM %s
                """.formatted(USER_TABLE_NAME), new MapSqlParameterSource(), Integer.class);

        int totalPages = (int) Math.ceil((double) totalElements / pageInfo.getPerPage());

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("page", pageInfo.getPage());
        parameters.addValue("perPage", pageInfo.getPerPage());

        List<UserProjection> content = jdbcTemplate.query("""
                SELECT id, username, password, display_name, role, locked
                FROM %s
                LIMIT :perPage
                OFFSET :page
                """.formatted(USER_TABLE_NAME), parameters, this::toProjection);

        return Page.<UserProjection>builder()
                .content(content)
                .info(PageInfo.builder()
                        .page(pageInfo.getPage())
                        .perPage(pageInfo.getPerPage())
                        .totalElements(totalElements)
                        .totalPages(totalPages)
                        .build())
                .build();
    }

    @Override
    public boolean existsById(UUID id) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", id);

        Integer count = jdbcTemplate.queryForObject("""
                SELECT COUNT(*)
                FROM %s
                WHERE id = :id
                """.formatted(USER_TABLE_NAME), parameters, Integer.class);

        return count != null && count > 0;
    }

    @SneakyThrows
    private UserProjection toProjection(ResultSet resultSet, int rowNum) {
        String username = resultSet.getString("username");
        String password = resultSet.getString("password");
        String displayName = resultSet.getString("display_name");
        Role role = Role.valueOf(resultSet.getString("role"));
        boolean locked = resultSet.getBoolean("locked");

        return UserProjection.builder()
                .id(UUID.fromString(resultSet.getString("id")))
                .username(username)
                .password(password)
                .displayName(displayName)
                .role(role)
                .locked(locked)
                .build();
    }
}
