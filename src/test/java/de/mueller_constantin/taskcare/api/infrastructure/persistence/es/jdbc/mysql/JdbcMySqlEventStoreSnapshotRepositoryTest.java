package de.mueller_constantin.taskcare.api.infrastructure.persistence.es.jdbc.mysql;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.mueller_constantin.taskcare.api.core.common.domain.Aggregate;
import de.mueller_constantin.taskcare.api.core.dummy.domain.DummyAggregate;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJdbcTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Sql(scripts = {"/sql/ddl/es/mysql.sql", "/sql/dml/es/mysql.sql"})
class JdbcMySqlEventStoreSnapshotRepositoryTest {
    @Autowired
    private JdbcMySqlEventStoreSnapshotRepository jdbcMySqlEventStoreSnapshotRepository;

    @Test
    @SneakyThrows
    void loadSnapshot() {
        Optional<Aggregate> aggregate = jdbcMySqlEventStoreSnapshotRepository.loadSnapshot(
                UUID.fromString("065e84bd-2e41-418c-82df-886d7e0c6f72"), 5);

        assertTrue(aggregate.isPresent());
        assertInstanceOf(DummyAggregate.class, aggregate.get());
        assertEquals(5, aggregate.get().getVersion());
    }

    @Test
    void loadSnapshotExcessiveVersion() {
        Optional<Aggregate> aggregate = jdbcMySqlEventStoreSnapshotRepository.loadSnapshot(
                UUID.fromString("065e84bd-2e41-418c-82df-886d7e0c6f72"), 6);

        assertTrue(aggregate.isPresent());
        assertInstanceOf(DummyAggregate.class, aggregate.get());
        assertEquals(5, aggregate.get().getVersion());
    }

    @Test
    void createSnapshot() {
        DummyAggregate dummyAggregate = new DummyAggregate(UUID.fromString("065e84bd-2e41-418c-82df-886d7e0c6f72"),
                0, false);

        jdbcMySqlEventStoreSnapshotRepository.createSnapshot(dummyAggregate);
    }

    @TestConfiguration
    static class JdbcMySqlEventStoreSnapshotRepositoryTestConfig {
        @Bean
        JdbcMySqlEventStoreSnapshotRepository jdbcMySqlEventStoreSnapshotRepository(NamedParameterJdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
            return new JdbcMySqlEventStoreSnapshotRepository(jdbcTemplate, objectMapper);
        }

        @Bean
        ObjectMapper objectMapper() {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            return objectMapper;
        }
    }
}