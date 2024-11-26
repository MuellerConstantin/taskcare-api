package de.mueller_constantin.taskcare.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.mueller_constantin.taskcare.api.infrastructure.persistence.es.EventStore;
import de.mueller_constantin.taskcare.api.infrastructure.persistence.es.jdbc.JdbcEventStore;
import de.mueller_constantin.taskcare.api.infrastructure.persistence.es.jdbc.JdbcEventStoreEventRepository;
import de.mueller_constantin.taskcare.api.infrastructure.persistence.es.jdbc.JdbcEventStoreMetadataRepository;
import de.mueller_constantin.taskcare.api.infrastructure.persistence.es.jdbc.JdbcEventStoreSnapshotRepository;
import de.mueller_constantin.taskcare.api.infrastructure.persistence.es.jdbc.mysql.JdbcMySqlEventStoreEventRepository;
import de.mueller_constantin.taskcare.api.infrastructure.persistence.es.jdbc.mysql.JdbcMySqlEventStoreMetadataRepository;
import de.mueller_constantin.taskcare.api.infrastructure.persistence.es.jdbc.mysql.JdbcMySqlEventStoreSnapshotRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@Configuration
public class PersistenceConfig {
    @Configuration
    public static class EventStoreConfig {
        @Bean
        EventStore eventStore(JdbcEventStoreEventRepository jdbcEventStoreEventRepository,
                              JdbcEventStoreMetadataRepository jdbcEventStoreMetadataRepository,
                              JdbcEventStoreSnapshotRepository jdbcEventStoreSnapshotRepository) {
            return new JdbcEventStore(jdbcEventStoreEventRepository,
                    jdbcEventStoreSnapshotRepository,
                    jdbcEventStoreMetadataRepository);
        }

        @Bean
        JdbcEventStoreEventRepository jdbcEventStoreEventRepository(NamedParameterJdbcTemplate jdbcTemplate,
                                                                    ObjectMapper objectMapper) {
            return new JdbcMySqlEventStoreEventRepository(jdbcTemplate, objectMapper);
        }

        @Bean
        JdbcEventStoreMetadataRepository jdbcEventStoreMetadataRepository(NamedParameterJdbcTemplate jdbcTemplate) {
            return new JdbcMySqlEventStoreMetadataRepository(jdbcTemplate);
        }

        @Bean
        JdbcEventStoreSnapshotRepository jdbcEventStoreSnapshotRepository(NamedParameterJdbcTemplate jdbcTemplate,
                                                                          ObjectMapper objectMapper) {
            return new JdbcMySqlEventStoreSnapshotRepository(jdbcTemplate, objectMapper);
        }
    }
}
