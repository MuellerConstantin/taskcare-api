package de.mueller_constantin.taskcare.api.infrastructure.persistence.crud.jdbc;

import de.mueller_constantin.taskcare.api.core.board.domain.ComponentProjection;
import de.mueller_constantin.taskcare.api.core.common.domain.Page;
import de.mueller_constantin.taskcare.api.core.common.domain.PageInfo;
import de.mueller_constantin.taskcare.api.infrastructure.persistence.crud.ComponentCrudRepository;
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
class MySqlComponentCrudRepositoryTest {
    @Autowired
    private ComponentCrudRepository componentCrudRepository;

    @Test
    void findById() {
        Optional<ComponentProjection> componentProjection = componentCrudRepository.findById(
                UUID.fromString("0ef48e1e-d77d-4335-8fd1-425d1b669014"));

        assertTrue(componentProjection.isPresent());
        assertEquals(UUID.fromString("0ef48e1e-d77d-4335-8fd1-425d1b669014"), componentProjection.get().getId());
    }

    @Test
    void findAllByBoardId() {
        Page<ComponentProjection> page = componentCrudRepository.findAllByBoardId(
                UUID.fromString("527c3e1e-6b2d-4887-a747-9dfb7cb3bb1e"),
                PageInfo.builder()
                        .page(0)
                        .perPage(1)
                        .build()
        );

        assertEquals(1, page.getContent().size());
        assertEquals(2, page.getInfo().getTotalElements());
        assertEquals(2, page.getInfo().getTotalPages());
    }

    @Test
    void saveAllForBoardId() {
        componentCrudRepository.saveAllForBoardId(
                UUID.fromString("527c3e1e-6b2d-4887-a747-9dfb7cb3bb1e"),
                List.of(
                        ComponentProjection.builder()
                                .id(UUID.fromString("0ef48e1e-d77d-4335-8fd1-425d1b669014"))
                                .boardId(UUID.fromString("527c3e1e-6b2d-4887-a747-9dfb7cb3bb1e"))
                                .name("Frontend")
                                .description("Tasks that are only related to the frontend")
                                .build(),
                        ComponentProjection.builder()
                                .id(UUID.randomUUID())
                                .boardId(UUID.fromString("527c3e1e-6b2d-4887-a747-9dfb7cb3bb1e"))
                                .name("DevOps")
                                .description("Tasks that are only related to DevOps")
                                .build()
                )
        );
    }

    @TestConfiguration
    static class MySqlComponentCrudRepositoryTestConfig {
        @Bean
        public ComponentCrudRepository mySqlComponentCrudRepository(NamedParameterJdbcTemplate jdbcTemplate) {
            return new MySqlComponentCrudRepository(jdbcTemplate);
        }
    }
}