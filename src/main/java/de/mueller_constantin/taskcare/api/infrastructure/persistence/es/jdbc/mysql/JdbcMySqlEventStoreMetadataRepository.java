package de.mueller_constantin.taskcare.api.infrastructure.persistence.es.jdbc.mysql;

import de.mueller_constantin.taskcare.api.core.common.domain.Aggregate;
import de.mueller_constantin.taskcare.api.infrastructure.persistence.es.jdbc.JdbcEventStoreMetadataRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Types;
import java.util.List;
import java.util.UUID;

@Transactional(propagation = Propagation.MANDATORY)
@RequiredArgsConstructor
public class JdbcMySqlEventStoreMetadataRepository implements JdbcEventStoreMetadataRepository {
    private final String METADATA_TABLE_NAME = "es_metadata";

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public void createMetadata(@NonNull Aggregate aggregate) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("aggregateId", aggregate.getId().toString());
        parameters.addValue("aggregateType", aggregate.getClass().getName());
        parameters.addValue("version", 0, Types.INTEGER);
        parameters.addValue("deleted", false, Types.BOOLEAN);

        jdbcTemplate.update("""
                INSERT IGNORE INTO %s (aggregate_id, aggregate_type, version, deleted)
                VALUES (:aggregateId, :aggregateType, :version, :deleted)
                """.formatted(METADATA_TABLE_NAME), parameters);
    }

    public void updateMetadata(@NonNull Aggregate aggregate) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("aggregateId", aggregate.getId().toString());
        parameters.addValue("version", aggregate.getVersion(), Types.INTEGER);
        parameters.addValue("deleted", aggregate.isDeleted(), Types.BOOLEAN);

        jdbcTemplate.update("""
                UPDATE %s
                SET version = :version, deleted = :deleted
                WHERE aggregate_id = :aggregateId
                """.formatted(METADATA_TABLE_NAME), parameters);
    }

    public List<UUID> loadAllAggregateIds(@NonNull Class<? extends Aggregate> aggregateClass,
                                          Integer limit, Integer offset) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("aggregateType", aggregateClass.getName());

        if(limit != null && offset != null) {
            parameters.addValue("limit", limit, Types.INTEGER);
            parameters.addValue("offset", offset, Types.INTEGER);

            return jdbcTemplate.queryForList("""
                    SELECT aggregate_id
                    FROM %s
                    WHERE aggregate_type = :aggregateType AND deleted = false
                    ORDER BY aggregate_id
                    LIMIT :limit
                    OFFSET :offset
                    """.formatted(METADATA_TABLE_NAME), parameters, UUID.class);
        } else {
            return jdbcTemplate.queryForList("""
                    SELECT aggregate_id
                    FROM %s
                    WHERE aggregate_type = :aggregateType AND deleted = false
                    ORDER BY aggregate_id
                    """.formatted(METADATA_TABLE_NAME), parameters, UUID.class);
        }
    }

    public int countAllAggregates(@NonNull Class<? extends Aggregate> aggregateClass) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("aggregateType", aggregateClass.getName());

        Integer count = jdbcTemplate.queryForObject("""
                SELECT COUNT(*)
                FROM %s
                WHERE aggregate_type = :aggregateType AND deleted = false
                """.formatted(METADATA_TABLE_NAME), parameters, Integer.class);

        return count == null ? 0 : count;
    }
}
