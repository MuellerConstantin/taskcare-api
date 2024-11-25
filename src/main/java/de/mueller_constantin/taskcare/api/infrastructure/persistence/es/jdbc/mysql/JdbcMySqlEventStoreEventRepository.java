package de.mueller_constantin.taskcare.api.infrastructure.persistence.es.jdbc.mysql;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.mueller_constantin.taskcare.api.core.common.domain.model.Event;
import de.mueller_constantin.taskcare.api.infrastructure.persistence.es.jdbc.JdbcEventStoreEventRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.Types;
import java.util.List;
import java.util.UUID;

@Transactional(propagation = Propagation.MANDATORY)
@RequiredArgsConstructor
public class JdbcMySqlEventStoreEventRepository implements JdbcEventStoreEventRepository {
    private final String METADATA_TABLE_NAME = "es_metadata";
    private final String EVENTS_TABLE_NAME = "es_events";

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    public void createEvent(@NonNull Event event) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("aggregateId", event.getAggregateId().toString());
        parameters.addValue("version", event.getVersion(), Types.INTEGER);
        parameters.addValue("eventType", event.getClass().getName());
        parameters.addValue("eventData", objectMapper.writeValueAsString(event));

        jdbcTemplate.update("""
                INSERT INTO %s (aggregate_id, version, event_type, event_data)
                VALUES (:aggregateId, :version, :eventType, :eventData)
                """.formatted(EVENTS_TABLE_NAME), parameters);
    }

    public List<Event> loadEvents(@NonNull UUID aggregateId, Integer fromVersion, Integer toVersion) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("aggregateId", aggregateId.toString());
        parameters.addValue("fromVersion", fromVersion, Types.INTEGER);
        parameters.addValue("toVersion", toVersion, Types.INTEGER);

        return jdbcTemplate.query("""
                SELECT
                    e.event_type as event_type,
                    e.event_data as event_data
                FROM %s e
                JOIN %s m ON e.aggregate_id = m.aggregate_id
                WHERE e.aggregate_id = :aggregateId
                    AND (:fromVersion IS NULL OR e.version > :fromVersion)
                    AND (:toVersion IS NULL OR e.version <= :toVersion)
                    AND m.deleted = false
                ORDER BY e.version ASC
                """.formatted(EVENTS_TABLE_NAME, METADATA_TABLE_NAME), parameters, this::toEvent);
    }

    @SneakyThrows
    private Event toEvent(ResultSet resultSet, int rowNum) {
        String eventType = resultSet.getString("event_type");
        String eventData = resultSet.getString("event_data");

        Class<? extends Event> aggregateClass = Class.forName(eventType).asSubclass(Event.class);

        return objectMapper.readValue(eventData, aggregateClass);
    }
}
