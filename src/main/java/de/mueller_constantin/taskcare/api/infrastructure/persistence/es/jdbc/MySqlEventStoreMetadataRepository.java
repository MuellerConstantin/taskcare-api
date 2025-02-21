package de.mueller_constantin.taskcare.api.infrastructure.persistence.es.jdbc;

import de.mueller_constantin.taskcare.api.core.common.domain.Aggregate;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Types;

@Transactional(propagation = Propagation.MANDATORY)
@RequiredArgsConstructor
public class MySqlEventStoreMetadataRepository implements JdbcEventStoreMetadataRepository {
    private final String METADATA_TABLE_NAME = "es_metadata";

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public void createMetadata(@NonNull Aggregate aggregate) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("aggregateId", aggregate.getId().toString());
        parameters.addValue("aggregateType", aggregate.getClass().getName());
        parameters.addValue("version", 0, Types.INTEGER);
        parameters.addValue("deleted", false, Types.BOOLEAN);

        String query = """
            INSERT IGNORE INTO %s (
                aggregate_id,
                aggregate_type,
                version,
                deleted
            ) VALUES (
                :aggregateId,
                :aggregateType,
                :version,
                :deleted
            )
        """.formatted(METADATA_TABLE_NAME);

        try {
            jdbcTemplate.update(query, parameters);
        } catch (DuplicateKeyException exc) {
            throw new OptimisticLockingFailureException(
                    "Optimistic locking failed for metadata (AggregateId: %s)".formatted(aggregate.getId()), exc);
        }
    }

    public void updateMetadata(@NonNull Aggregate aggregate) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("aggregateId", aggregate.getId().toString());
        parameters.addValue("version", aggregate.getVersion(), Types.INTEGER);
        parameters.addValue("expectedVersion", aggregate.getCommittedVersion(), Types.INTEGER);
        parameters.addValue("deleted", aggregate.isDeleted(), Types.BOOLEAN);

        String query = """
            UPDATE %s
            SET
                version = :version,
                deleted = :deleted
            WHERE aggregate_id = :aggregateId AND version = :expectedVersion
        """.formatted(METADATA_TABLE_NAME);

        int updated = jdbcTemplate.update(query, parameters);

        if (updated == 0) {
            throw new OptimisticLockingFailureException("Optimistic locking failed for metadata (AggregateId: %s; Version %d)".formatted(aggregate.getId(), aggregate.getVersion()));
        }
    }
}
