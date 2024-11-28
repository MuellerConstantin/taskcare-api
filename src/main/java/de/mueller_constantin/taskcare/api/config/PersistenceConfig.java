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
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@Configuration
public class PersistenceConfig {
    @Bean
    JedisConnectionFactory jedisConnectionFactory(RedisProperties redisProperties) {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(redisProperties.getHost());
        redisStandaloneConfiguration.setPort(redisProperties.getPort());
        redisStandaloneConfiguration.setDatabase(redisProperties.getDatabase());
        redisStandaloneConfiguration.setUsername(redisProperties.getUsername());
        redisStandaloneConfiguration.setPassword(redisProperties.getPassword());

        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }

    @Bean
    RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        final RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setDefaultSerializer(new StringRedisSerializer());
        return redisTemplate;
    }

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
