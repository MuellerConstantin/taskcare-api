package de.mueller_constantin.taskcare.api.infrastructure.persistence;

import de.mueller_constantin.taskcare.api.core.common.domain.Page;
import de.mueller_constantin.taskcare.api.core.common.domain.PageInfo;
import de.mueller_constantin.taskcare.api.core.user.domain.UserProjection;
import de.mueller_constantin.taskcare.api.infrastructure.persistence.es.EventStore;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJdbcTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Sql(scripts = {"/sql/ddl/mysql.sql", "/sql/dml/mysql.sql"})
class JdbcMySqlUserRepositoryTest {
    @MockitoBean
    private EventStore eventStore;

    @Autowired
    private JdbcMySqlUserRepository jdbcMySqlUserRepository;

    @Test
    void findById() {
        Optional<UserProjection> user = jdbcMySqlUserRepository.findById(
                UUID.fromString("8d031fe3-e445-4d51-8c70-ac3e3810da87"));

        assertTrue(user.isPresent());
        assertEquals(UUID.fromString("8d031fe3-e445-4d51-8c70-ac3e3810da87"), user.get().getId());
    }

    @Test
    void findByUsername() {
        Optional<UserProjection> user = jdbcMySqlUserRepository.findByUsername("maxi123");

        assertTrue(user.isPresent());
        assertEquals("maxi123", user.get().getUsername());
    }

    @Test
    void existsByUsername() {
        boolean exists = jdbcMySqlUserRepository.existsByUsername("maxi123");

        assertTrue(exists);
    }

    @Test
    void existsById() {
        boolean exists = jdbcMySqlUserRepository.existsById(UUID.fromString("8d031fe3-e445-4d51-8c70-ac3e3810da87"));

        assertTrue(exists);
    }

    @Test
    void findAll() {
        List<UserProjection> users = jdbcMySqlUserRepository.findAll();

        assertEquals(2, users.size());
    }

    @Test
    void findAllPaged() {
        Page<UserProjection> page = jdbcMySqlUserRepository.findAll(PageInfo.builder()
                .page(0)
                .perPage(1)
                .build());

        assertEquals(1, page.getContent().size());
        assertEquals(2, page.getInfo().getTotalElements());
        assertEquals(2, page.getInfo().getTotalPages());
    }

    @TestConfiguration
    static class JdbcUserRepositoryTestConfig {
        @Bean
        JdbcMySqlUserRepository jdbcUserRepository(NamedParameterJdbcTemplate jdbcTemplate, EventStore eventStore) {
            return new JdbcMySqlUserRepository(jdbcTemplate, eventStore);
        }
    }
}
