package de.mueller_constantin.taskcare.api.infrastructure.persistence.es.jdbc.mysql;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import de.mueller_constantin.taskcare.api.core.common.domain.Aggregate;
import de.mueller_constantin.taskcare.api.infrastructure.persistence.es.jdbc.JdbcEventStoreSnapshotRepository;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.Types;
import java.util.Optional;
import java.util.UUID;

@Transactional(propagation = Propagation.MANDATORY)
public class JdbcMySqlEventStoreSnapshotRepository implements JdbcEventStoreSnapshotRepository {
    private final String METADATA_TABLE_NAME = "es_metadata";
    private final String SNAPSHOT_TABLE_NAME = "es_snapshots";

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;
    private final ObjectWriter objectWriter;

    public JdbcMySqlEventStoreSnapshotRepository(NamedParameterJdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;

        this.objectMapper.addMixIn(Aggregate.class, AggregateJsonFilterMixin.class);

        SimpleFilterProvider filterProvider = new SimpleFilterProvider().addFilter("aggregateDataFilter",
                SimpleBeanPropertyFilter.serializeAllExcept("uncommittedEvents", "committedVersion"));

        this.objectWriter = this.objectMapper.writer(filterProvider);
    }

    @SneakyThrows
    public void createSnapshot(@NonNull Aggregate aggregate) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("aggregateId", aggregate.getId().toString());
        parameters.addValue("version", aggregate.getVersion(), Types.INTEGER);
        parameters.addValue("aggregateData", objectWriter.writeValueAsString(aggregate));

        System.out.println(objectWriter.writeValueAsString(aggregate));

        jdbcTemplate.update("""
                INSERT INTO %s (aggregate_id, version, aggregate_data)
                VALUES (:aggregateId, :version, :aggregateData)
                """.formatted(SNAPSHOT_TABLE_NAME), parameters);
    }

    public <T extends Aggregate> Optional<T> loadSnapshot(@NonNull UUID aggregateId, Integer version) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("aggregateId", aggregateId.toString());
        parameters.addValue("version", version, Types.INTEGER);

        return jdbcTemplate.query("""
                SELECT
                    m.aggregate_type as aggregate_type,
                    s.aggregate_data as aggregate_data
                FROM %s s
                JOIN %s m ON s.aggregate_id = m.aggregate_id
                WHERE s.aggregate_id = :aggregateId
                    AND (:version IS NULL OR s.version <= :version)
                    AND m.deleted = false
                ORDER BY s.version DESC
                LIMIT 1
                """.formatted(SNAPSHOT_TABLE_NAME, METADATA_TABLE_NAME), parameters, this::toAggregate)
                .stream()
                .findFirst()
                .map(aggregate -> (T) aggregate);
    }

    @SneakyThrows
    private Aggregate toAggregate(ResultSet resultSet, int rowNum) {
        String aggregateType = resultSet.getString("aggregate_type");
        String aggregateData = resultSet.getString("aggregate_data");

        Class<? extends Aggregate> aggregateClass = Class.forName(aggregateType).asSubclass(Aggregate.class);

        return objectMapper.readValue(aggregateData, aggregateClass);
    }

    @JsonFilter("aggregateDataFilter")
    public static class AggregateJsonFilterMixin {
    }
}
