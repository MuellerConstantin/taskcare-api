package de.mueller_constantin.taskcare.api.infrastructure.persistence;

import de.mueller_constantin.taskcare.api.core.common.domain.Page;
import de.mueller_constantin.taskcare.api.core.common.domain.PageInfo;
import de.mueller_constantin.taskcare.api.core.kanban.domain.BoardAggregate;
import de.mueller_constantin.taskcare.api.core.kanban.domain.BoardProjection;
import de.mueller_constantin.taskcare.api.infrastructure.persistence.crud.BoardCrudRepository;
import de.mueller_constantin.taskcare.api.infrastructure.persistence.crud.jdbc.MySqlBoardCrudRepository;
import de.mueller_constantin.taskcare.api.infrastructure.persistence.es.EventStore;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@ExtendWith(SpringExtension.class)
@DataJdbcTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Sql(scripts = {"/sql/ddl/mysql.sql", "/sql/dml/mysql.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@TestPropertySource(properties = {
        "logging.level.org.springframework.jdbc.core=TRACE"
})
class BoardRepositoryTest {
    @MockitoBean
    private EventStore eventStore;

    @MockitoBean
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private BoardRepository boardRepository;

    @Test
    void findById() {
        Optional<BoardProjection> boardProjection = boardRepository.findById(
                UUID.fromString("527c3e1e-6b2d-4887-a747-9dfb7cb3bb1e"));

        assertTrue(boardProjection.isPresent());
        assertEquals(UUID.fromString("527c3e1e-6b2d-4887-a747-9dfb7cb3bb1e"), boardProjection.get().getId());
        assertEquals(2, boardProjection.get().getMembers().size());
    }

    @Test
    void existsById() {
        boolean exists = boardRepository.existsById(UUID.fromString("527c3e1e-6b2d-4887-a747-9dfb7cb3bb1e"));

        assertTrue(exists);
    }

    @Test
    void findAll() {
        List<BoardProjection> users = boardRepository.findAll();

        assertEquals(2, users.size());
    }

    @Test
    void findAllPaged() {
        Page<BoardProjection> page = boardRepository.findAll(PageInfo.builder()
                .page(0)
                .perPage(1)
                .build());

        assertEquals(1, page.getContent().size());
        assertEquals(2, page.getInfo().getTotalElements());
        assertEquals(2, page.getInfo().getTotalPages());
    }

    @Test
    void findAllUserIsMemberPaged() {
        Page<BoardProjection> page = boardRepository.findAllUserIsMember(UUID.fromString("6aa18950-81e0-4ac4-ad3a-37437db5c957"), PageInfo.builder()
                .page(0)
                .perPage(1)
                .build());

        assertEquals(1, page.getContent().size());
        assertEquals(2, page.getInfo().getTotalElements());
        assertEquals(2, page.getInfo().getTotalPages());
    }

    @Test
    void findAllUserIsMember() {
        List<BoardProjection> boardProjections = boardRepository.findAllUserIsMember(UUID.fromString("6aa18950-81e0-4ac4-ad3a-37437db5c957"));

        assertEquals(2, boardProjections.size());
    }

    @Test
    void save() {
        doNothing().when(eventStore).saveAggregate(any());
        doNothing().when(applicationEventPublisher).publishEvent(any());

        BoardAggregate aggregate = new BoardAggregate();
        aggregate.create("Kanban #3",
                "Kanban Board #3",
                UUID.fromString("8d031fe3-e445-4d51-8c70-ac3e3810da87"));

        boardRepository.save(aggregate);
    }

    @TestConfiguration
    static class BoardDomainRepositoryTestConfig {
        @Bean
        BoardRepository boardDomainRepository(EventStore eventStore,
                                              BoardCrudRepository boardCrudRepository,
                                              ApplicationEventPublisher applicationEventPublisher) {
            return new BoardRepository(eventStore, boardCrudRepository, applicationEventPublisher);
        }

        @Bean
        BoardCrudRepository mysqlBoardCrudRepository(NamedParameterJdbcTemplate jdbcTemplate) {
            return new MySqlBoardCrudRepository(jdbcTemplate);
        }
    }
}
