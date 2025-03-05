package de.mueller_constantin.taskcare.api.infrastructure.persistence.crud.jdbc;

import de.mueller_constantin.taskcare.api.core.task.domain.TaskProjection;
import de.mueller_constantin.taskcare.api.infrastructure.persistence.crud.TaskCrudRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJdbcTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Sql(scripts = {"/sql/ddl/mysql.sql", "/sql/dml/mysql.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@TestPropertySource(properties = {
        "logging.level.org.springframework.jdbc.core=TRACE"
})
class MySqlTaskCrudRepositoryTest {
    @Autowired
    private MySqlTaskCrudRepository taskCrudRepository;

    @Test
    void findById() {
        Optional<TaskProjection> taskProjection = taskCrudRepository.findById(
                UUID.fromString("7e391a2f-a5b9-4223-b789-021eae4db029"));

        assertTrue(taskProjection.isPresent());
        assertEquals(UUID.fromString("7e391a2f-a5b9-4223-b789-021eae4db029"), taskProjection.get().getId());
    }

    @TestConfiguration
    static class MySqlTaskCrudRepositoryTestConfiguration {
        @Bean
        public TaskCrudRepository mySqlTaskCrudRepository(NamedParameterJdbcTemplate jdbcTemplate) {
            return new MySqlTaskCrudRepository(jdbcTemplate);
        }
    }
}