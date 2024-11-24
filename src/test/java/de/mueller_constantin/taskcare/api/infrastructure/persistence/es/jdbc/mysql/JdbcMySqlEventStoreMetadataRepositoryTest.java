package de.mueller_constantin.taskcare.api.infrastructure.persistence.es.jdbc.mysql;

import de.mueller_constantin.taskcare.api.core.dummy.domain.DummyAggregate;
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

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJdbcTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Sql(scripts = {"/sql/ddl/es/mysql.sql", "/sql/dml/es/mysql.sql"})
class JdbcMySqlEventStoreMetadataRepositoryTest {
    @Autowired
    private JdbcMySqlEventStoreMetadataRepository jdbcMySqlEventStoreMetadataRepository;

    @Test
    void save() {
        DummyAggregate dummyAggregate = new DummyAggregate();
        dummyAggregate.create("value1", "value2");

        jdbcMySqlEventStoreMetadataRepository.createMetadata(dummyAggregate);

        int count = jdbcMySqlEventStoreMetadataRepository.countAllAggregates(DummyAggregate.class);

        assertEquals(3, count);

        List<UUID> aggregateIds = jdbcMySqlEventStoreMetadataRepository.loadAllAggregateIds(DummyAggregate.class, null, null);

        assertEquals(3, aggregateIds.size());
        assertTrue(aggregateIds.contains(dummyAggregate.getId()));
    }

    @Test
    void countAllAggregates() {
        int count = jdbcMySqlEventStoreMetadataRepository.countAllAggregates(DummyAggregate.class);

        assertEquals(2, count);
    }

    @Test
    void loadAllAggregateIds() {
        List<UUID> aggregateIds = jdbcMySqlEventStoreMetadataRepository.loadAllAggregateIds(DummyAggregate.class, null, null);

        assertEquals(2, aggregateIds.size());
    }

    @TestConfiguration
    static class JdbcMySqlEventStoreMetadataRepositoryTestConfig {

        @Bean
        JdbcMySqlEventStoreMetadataRepository jdbcEventStoreMetadataRepository(NamedParameterJdbcTemplate jdbcTemplate) {
            return new JdbcMySqlEventStoreMetadataRepository(jdbcTemplate);
        }
    }
}
