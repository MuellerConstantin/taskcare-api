package de.mueller_constantin.taskcare.api.infrastructure.persistence.crud.jdbc;

import de.mueller_constantin.taskcare.api.core.common.domain.Page;
import de.mueller_constantin.taskcare.api.core.common.domain.PageInfo;
import de.mueller_constantin.taskcare.api.core.board.domain.MemberProjection;
import de.mueller_constantin.taskcare.api.core.board.domain.Role;
import de.mueller_constantin.taskcare.api.infrastructure.persistence.crud.MemberCrudRepository;
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
class MySqlMemberCrudRepositoryTest {
    @Autowired
    private MemberCrudRepository memberCrudRepository;

    @Test
    void findById() {
        Optional<MemberProjection> memberProjection = memberCrudRepository.findById(
                UUID.fromString("99bb36fb-0f5b-458a-8176-cc9d49faea3d"));

        assertTrue(memberProjection.isPresent());
        assertEquals(UUID.fromString("99bb36fb-0f5b-458a-8176-cc9d49faea3d"), memberProjection.get().getId());
    }

    @Test
    void findAllByBoardId() {
        Page<MemberProjection> page = memberCrudRepository.findAllByBoardId(
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
        memberCrudRepository.saveAllForBoardId(
                UUID.fromString("527c3e1e-6b2d-4887-a747-9dfb7cb3bb1e"),
                List.of(
                        MemberProjection.builder()
                                .id(UUID.fromString("13d65e6a-daae-4fdb-83ab-4ff398cd2300"))
                                .boardId(UUID.fromString("527c3e1e-6b2d-4887-a747-9dfb7cb3bb1e"))
                                .userId(UUID.fromString("6aa18950-81e0-4ac4-ad3a-37437db5c957"))
                                .role(Role.MAINTAINER)
                                .build(),
                        MemberProjection.builder()
                                .id(UUID.randomUUID())
                                .boardId(UUID.fromString("527c3e1e-6b2d-4887-a747-9dfb7cb3bb1e"))
                                .userId(UUID.fromString("7c559b1c-82b6-4e91-8e8e-c637c0bbda14"))
                                .role(Role.MEMBER)
                                .build()
                )
        );
    }

    @TestConfiguration
    static class MySqlMemberCrudRepositoryTestConfig {
        @Bean
        MemberCrudRepository mysqlMemberCrudRepository(NamedParameterJdbcTemplate jdbcTemplate) {
            return new MySqlMemberCrudRepository(jdbcTemplate);
        }
    }
}