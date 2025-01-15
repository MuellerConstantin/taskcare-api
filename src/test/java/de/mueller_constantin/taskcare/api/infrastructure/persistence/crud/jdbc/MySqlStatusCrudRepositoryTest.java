package de.mueller_constantin.taskcare.api.infrastructure.persistence.crud.jdbc;

import de.mueller_constantin.taskcare.api.core.common.domain.Page;
import de.mueller_constantin.taskcare.api.core.common.domain.PageInfo;
import de.mueller_constantin.taskcare.api.core.board.domain.StatusProjection;
import de.mueller_constantin.taskcare.api.infrastructure.persistence.crud.StatusCrudRepository;
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

import java.util.List;
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
class MySqlStatusCrudRepositoryTest {
    @Autowired
    private StatusCrudRepository statusCrudRepository;

    @Test
    void findById() {
        Optional<StatusProjection> memberProjection = statusCrudRepository.findById(
                UUID.fromString("09271e9a-c145-4a6b-92e4-2bc0d5a0710a"));

        assertTrue(memberProjection.isPresent());
        assertEquals(UUID.fromString("09271e9a-c145-4a6b-92e4-2bc0d5a0710a"), memberProjection.get().getId());
    }

    @Test
    void findAllByBoardId() {
        Page<StatusProjection> page = statusCrudRepository.findAllByBoardId(
                UUID.fromString("527c3e1e-6b2d-4887-a747-9dfb7cb3bb1e"),
                PageInfo.builder()
                        .page(0)
                        .perPage(1)
                        .build()
        );

        assertEquals(1, page.getContent().size());
        assertEquals(3, page.getInfo().getTotalElements());
        assertEquals(3, page.getInfo().getTotalPages());
    }

    @Test
    void saveAllForBoardId() {
        statusCrudRepository.saveAllForBoardId(
                UUID.fromString("527c3e1e-6b2d-4887-a747-9dfb7cb3bb1e"),
                List.of(
                        StatusProjection.builder()
                                .id(UUID.fromString("9e3baa5e-0f1c-4ad1-b3d9-19f48e02c0a2"))
                                .boardId(UUID.fromString("527c3e1e-6b2d-4887-a747-9dfb7cb3bb1e"))
                                .name("Done")
                                .description("Task has been finished")
                                .build(),
                        StatusProjection.builder()
                                .id(UUID.randomUUID())
                                .boardId(UUID.fromString("527c3e1e-6b2d-4887-a747-9dfb7cb3bb1e"))
                                .name("Archived")
                                .description("Task has been archived")
                                .build()
                )
        );
    }

    @TestConfiguration
    static class MySqlStatusCrudRepositoryTestConfig {
        @Bean
        StatusCrudRepository mysqlStatusCrudRepository(NamedParameterJdbcTemplate jdbcTemplate) {
            return new MySqlStatusCrudRepository(jdbcTemplate);
        }
    }
}