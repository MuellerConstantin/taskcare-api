package de.mueller_constantin.taskcare.api.infrastructure.persistence.crud.jdbc;

import de.mueller_constantin.taskcare.api.core.common.domain.Page;
import de.mueller_constantin.taskcare.api.core.common.domain.PageInfo;
import de.mueller_constantin.taskcare.api.core.user.domain.IdentityProvider;
import de.mueller_constantin.taskcare.api.core.user.domain.Role;
import de.mueller_constantin.taskcare.api.core.user.domain.UserProjection;
import de.mueller_constantin.taskcare.api.infrastructure.persistence.crud.UserCrudRepository;
import de.mueller_constantin.taskcare.api.infrastructure.persistence.crud.jdbc.rsql.MySqlUserRSQLConverter;
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
public class MySqlUserCrudRepository implements UserCrudRepository {
    private final String USER_TABLE_NAME = "users";

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public Optional<UserProjection> findByUsername(String username) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("username", username);

        String query = """
            SELECT
                id,
                username,
                password,
                display_name,
                role,
                identity_provider,
                locked
            FROM %s
            WHERE username = :username
        """.formatted(USER_TABLE_NAME);

        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(query, parameters, this::toProjection));
        } catch (EmptyResultDataAccessException exc) {
            return Optional.empty();
        }
    }

    @Override
    public boolean existsByUsername(String username) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("username", username);

        String query = """
            SELECT
                COUNT(*)
            FROM %s
            WHERE username = :username
        """.formatted(USER_TABLE_NAME);

        Integer count = jdbcTemplate.queryForObject(query, parameters, Integer.class);

        return count != null && count > 0;
    }

    @Override
    public Optional<UserProjection> findById(UUID id) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", id.toString());

        String query = """
            SELECT
                id,
                username,
                password,
                display_name,
                role,
                identity_provider,
                locked
            FROM %s
            WHERE id = :id
        """.formatted(USER_TABLE_NAME);

        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(query, parameters, this::toProjection));
        } catch (EmptyResultDataAccessException exc) {
            return Optional.empty();
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
        """.formatted(USER_TABLE_NAME);

        Integer count = jdbcTemplate.queryForObject(query, parameters, Integer.class);

        return count != null && count > 0;
    }

    @Override
    public List<UserProjection> findAll() {
        String query = """
            SELECT
                id,
                username,
                password,
                display_name,
                role,
                identity_provider,
                locked
            FROM %s
        """.formatted(USER_TABLE_NAME);

        return jdbcTemplate.query(query, this::toProjection);
    }

    @Override
    public Page<UserProjection> findAll(PageInfo pageInfo) {
        String query = """
            SELECT
                COUNT(*)
            FROM %s
        """.formatted(USER_TABLE_NAME);

        Integer totalElements = jdbcTemplate.queryForObject(query, new MapSqlParameterSource(), Integer.class);
        int totalPages = (int) Math.ceil((double) totalElements / pageInfo.getPerPage());

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("offset", pageInfo.getPage() * pageInfo.getPerPage());
        parameters.addValue("limit", pageInfo.getPerPage());

        query = """
            SELECT
                id,
                username,
                password,
                display_name,
                role,
                identity_provider,
                locked
            FROM %s
            LIMIT :limit
            OFFSET :offset
        """.formatted(USER_TABLE_NAME);

        List<UserProjection> content = jdbcTemplate.query(query, parameters, this::toProjection);

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
    public Page<UserProjection> findAll(PageInfo pageInfo, String predicate) {
        if(predicate == null) {
            return findAll(pageInfo);
        }

        MySqlUserRSQLConverter converter = new MySqlUserRSQLConverter();
        converter.parse(predicate);

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValues(converter.getParameters().getValues());

        String query = """
            SELECT
                COUNT(*)
            FROM %s
            WHERE %s
        """.formatted(USER_TABLE_NAME, converter.getQuery());

        Integer totalElements = jdbcTemplate.queryForObject(query, parameters, Integer.class);
        int totalPages = (int) Math.ceil((double) totalElements / pageInfo.getPerPage());

        parameters = new MapSqlParameterSource();
        parameters.addValue("offset", pageInfo.getPage() * pageInfo.getPerPage());
        parameters.addValue("limit", pageInfo.getPerPage());
        parameters.addValues(converter.getParameters().getValues());

        query = """
            SELECT
                id,
                username,
                password,
                display_name,
                role,
                identity_provider,
                locked
            FROM %s
            WHERE %s
            LIMIT :limit
            OFFSET :offset
        """.formatted(USER_TABLE_NAME, converter.getQuery());

        List<UserProjection> content = jdbcTemplate.query(query, parameters, this::toProjection);

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
    public void deleteById(UUID id) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", id.toString());

        String query = """
            DELETE
            FROM %s
            WHERE id = :id
        """.formatted(USER_TABLE_NAME);

        jdbcTemplate.update(query, parameters);
    }

    @Override
    public void save(UserProjection projection) {
        boolean exists = existsById(projection.getId());

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", projection.getId().toString());
        parameters.addValue("username", projection.getUsername());
        parameters.addValue("password", projection.getPassword());
        parameters.addValue("displayName", projection.getDisplayName());
        parameters.addValue("role", projection.getRole().toString());
        parameters.addValue("identityProvider", projection.getIdentityProvider().toString());
        parameters.addValue("locked", projection.isLocked(), Types.BOOLEAN);

        String query;

        if(exists) {
            query = """
                UPDATE %s
                SET
                    username = :username,
                    password = :password,
                    display_name = :displayName,
                    role = :role,
                    identity_provider = :identityProvider,
                    locked = :locked
                WHERE id = :id
            """.formatted(USER_TABLE_NAME);
        } else {
            query = """
                INSERT INTO %s (
                    id,
                    username,
                    password,
                    display_name,
                    role,
                    identity_provider,
                    locked
                ) VALUES (
                    :id,
                    :username,
                    :password,
                    :displayName,
                    :role,
                    :identityProvider,
                    :locked
                )
            """.formatted(USER_TABLE_NAME);
        }

        jdbcTemplate.update(query, parameters);
    }

    @SneakyThrows
    private UserProjection toProjection(ResultSet resultSet, int rowNum) {
        UUID id = UUID.fromString(resultSet.getString("id"));
        String username = resultSet.getString("username");
        String password = resultSet.getString("password");
        String displayName = resultSet.getString("display_name");
        Role role = Role.valueOf(resultSet.getString("role"));
        IdentityProvider identityProvider = IdentityProvider.valueOf(resultSet.getString("identity_provider"));
        boolean locked = resultSet.getBoolean("locked");

        return UserProjection.builder()
                .id(id)
                .username(username)
                .password(password)
                .displayName(displayName)
                .role(role)
                .identityProvider(identityProvider)
                .locked(locked)
                .build();
    }
}
