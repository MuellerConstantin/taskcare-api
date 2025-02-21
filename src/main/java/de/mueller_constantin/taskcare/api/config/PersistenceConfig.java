package de.mueller_constantin.taskcare.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.mueller_constantin.taskcare.api.infrastructure.persistence.MinioProperties;
import de.mueller_constantin.taskcare.api.infrastructure.persistence.es.EventStore;
import de.mueller_constantin.taskcare.api.infrastructure.persistence.es.jdbc.JdbcEventStore;
import de.mueller_constantin.taskcare.api.infrastructure.persistence.es.jdbc.JdbcEventStoreEventRepository;
import de.mueller_constantin.taskcare.api.infrastructure.persistence.es.jdbc.JdbcEventStoreMetadataRepository;
import de.mueller_constantin.taskcare.api.infrastructure.persistence.es.jdbc.JdbcEventStoreSnapshotRepository;
import de.mueller_constantin.taskcare.api.infrastructure.persistence.es.jdbc.MySqlEventStoreEventRepository;
import de.mueller_constantin.taskcare.api.infrastructure.persistence.es.jdbc.MySqlEventStoreMetadataRepository;
import de.mueller_constantin.taskcare.api.infrastructure.persistence.es.jdbc.MySqlEventStoreSnapshotRepository;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.retry.annotation.EnableRetry;

@Configuration
@EnableRetry
public class PersistenceConfig {
    @Autowired
    private MinioProperties minioProperties;

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

    @Bean
    public RedisLockRegistry redisLockRegistry(RedisConnectionFactory redisConnectionFactory) {
        return new RedisLockRegistry(redisConnectionFactory, "persistenceLock"); // "my-lock-key" ist ein globaler Schlüssel
    }

    @Bean
    MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(minioProperties.getEndpoint())
                .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                .build();
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
            return new MySqlEventStoreEventRepository(jdbcTemplate, objectMapper);
        }

        @Bean
        JdbcEventStoreMetadataRepository jdbcEventStoreMetadataRepository(NamedParameterJdbcTemplate jdbcTemplate) {
            return new MySqlEventStoreMetadataRepository(jdbcTemplate);
        }

        @Bean
        JdbcEventStoreSnapshotRepository jdbcEventStoreSnapshotRepository(NamedParameterJdbcTemplate jdbcTemplate,
                                                                          ObjectMapper objectMapper) {
            return new MySqlEventStoreSnapshotRepository(jdbcTemplate, objectMapper);
        }
    }
}
