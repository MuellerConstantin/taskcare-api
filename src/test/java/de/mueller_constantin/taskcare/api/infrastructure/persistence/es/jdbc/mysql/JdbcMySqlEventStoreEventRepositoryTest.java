package de.mueller_constantin.taskcare.api.infrastructure.persistence.es.jdbc.mysql;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.mueller_constantin.taskcare.api.core.common.domain.Event;
import de.mueller_constantin.taskcare.api.core.dummy.domain.DummyCreatedEvent;
import de.mueller_constantin.taskcare.api.core.dummy.domain.DummyUpdatedEvent;
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

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJdbcTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Sql(scripts = {"/sql/ddl/es/mysql.sql", "/sql/dml/es/mysql.sql"})
class JdbcMySqlEventStoreEventRepositoryTest {
    @Autowired
    private JdbcMySqlEventStoreEventRepository jdbcMySqlEventStoreEventRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void loadEvents() {
        List<Event> events = jdbcMySqlEventStoreEventRepository.loadEvents(
                UUID.fromString("065e84bd-2e41-418c-82df-886d7e0c6f72"), 0, null);

        assertEquals(6, events.size());
        assertEquals("de.mueller_constantin.taskcare.api.core.dummy.domain.DummyCreatedEvent", events.get(0).getClass().getName());
        assertInstanceOf(DummyCreatedEvent.class, events.get(0));
    }

    @Test
    @SneakyThrows
    void createEvent() {
        DummyUpdatedEvent event = DummyUpdatedEvent.builder()
                .aggregateId(UUID.fromString("065e84bd-2e41-418c-82df-886d7e0c6f72"))
                .version(7)
                .property1("newValue1")
                .property2("newValue2")
                .build();

        System.out.println(objectMapper.writeValueAsString(event));

        jdbcMySqlEventStoreEventRepository.createEvent(event);

        List<Event> events = jdbcMySqlEventStoreEventRepository.loadEvents(
                UUID.fromString("065e84bd-2e41-418c-82df-886d7e0c6f72"), null, null);

        assertEquals(7, events.size());
        assertEquals("de.mueller_constantin.taskcare.api.core.dummy.domain.DummyUpdatedEvent", events.get(6).getClass().getName());
        assertInstanceOf(DummyUpdatedEvent.class, events.get(6));
    }

    @TestConfiguration
    static class JdbcMySqlEventStoreEventRepositoryTestConfig {
        @Bean
        JdbcMySqlEventStoreEventRepository jdbcMySqlEventStoreEventRepository(NamedParameterJdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
            return new JdbcMySqlEventStoreEventRepository(jdbcTemplate, objectMapper);
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