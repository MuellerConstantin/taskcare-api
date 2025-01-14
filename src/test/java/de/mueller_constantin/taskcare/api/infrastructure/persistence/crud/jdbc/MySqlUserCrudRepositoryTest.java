package de.mueller_constantin.taskcare.api.infrastructure.persistence.crud.jdbc;

import de.mueller_constantin.taskcare.api.core.common.domain.Page;
import de.mueller_constantin.taskcare.api.core.common.domain.PageInfo;
import de.mueller_constantin.taskcare.api.core.user.domain.IdentityProvider;
import de.mueller_constantin.taskcare.api.core.user.domain.Role;
import de.mueller_constantin.taskcare.api.core.user.domain.UserProjection;
import de.mueller_constantin.taskcare.api.infrastructure.persistence.crud.UserCrudRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DuplicateKeyException;
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
class MySqlUserCrudRepositoryTest {
    @Autowired
    private UserCrudRepository userCrudRepository;

    @Test
    void save() {
        UserProjection userProjection = UserProjection.builder()
                .id(UUID.randomUUID())
                .username("gerd123")
                .password("$2y$10$xHTfzAglrIZISiZf3vL8yeBP.1C9aBHlvof2kRoEF7YMV0YjdR75G")
                .role(Role.USER)
                .identityProvider(IdentityProvider.LOCAL)
                .build();

        userCrudRepository.save(userProjection);
    }

    @Test
    void saveWithExistingUsername() {
        UserProjection userProjection = UserProjection.builder()
                .id(UUID.randomUUID())
                .username("maxi123")
                .password("$2y$10$xHTfzAglrIZISiZf3vL8yeBP.1C9aBHlvof2kRoEF7YMV0YjdR75G")
                .role(Role.USER)
                .identityProvider(IdentityProvider.LOCAL)
                .build();

        assertThrows(DuplicateKeyException.class, () -> userCrudRepository.save(userProjection));
    }

    @Test
    void findById() {
        Optional<UserProjection> user = userCrudRepository.findById(
                UUID.fromString("8d031fe3-e445-4d51-8c70-ac3e3810da87"));

        assertTrue(user.isPresent());
        assertEquals(UUID.fromString("8d031fe3-e445-4d51-8c70-ac3e3810da87"), user.get().getId());
    }

    @Test
    void findByUsername() {
        Optional<UserProjection> user = userCrudRepository.findByUsername("maxi123");

        assertTrue(user.isPresent());
        assertEquals("maxi123", user.get().getUsername());
    }

    @Test
    void existsByUsername() {
        boolean exists = userCrudRepository.existsByUsername("maxi123");

        assertTrue(exists);
    }

    @Test
    void existsById() {
        boolean exists = userCrudRepository.existsById(UUID.fromString("8d031fe3-e445-4d51-8c70-ac3e3810da87"));

        assertTrue(exists);
    }

    @Test
    void findAll() {
        List<UserProjection> users = userCrudRepository.findAll();

        assertEquals(3, users.size());
    }

    @Test
    void findAllPaged() {
        Page<UserProjection> page = userCrudRepository.findAll(PageInfo.builder()
                .page(0)
                .perPage(1)
                .build());

        assertEquals(1, page.getContent().size());
        assertEquals(3, page.getInfo().getTotalElements());
        assertEquals(3, page.getInfo().getTotalPages());
    }

    @TestConfiguration
    static class MySqlUserCrudRepositoryTestConfig {
        @Bean
        UserCrudRepository mysqlUserCrudRepository(NamedParameterJdbcTemplate jdbcTemplate) {
            return new MySqlUserCrudRepository(jdbcTemplate);
        }
    }
}