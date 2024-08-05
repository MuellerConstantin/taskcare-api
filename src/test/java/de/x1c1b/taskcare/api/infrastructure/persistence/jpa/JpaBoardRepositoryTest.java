package de.x1c1b.taskcare.api.infrastructure.persistence.jpa;

import de.x1c1b.taskcare.api.infrastructure.persistence.jpa.entity.mapper.BoardEntityMapper;
import de.x1c1b.taskcare.api.infrastructure.persistence.jpa.entity.mapper.BoardEntityMapperImpl;
import de.x1c1b.taskcare.api.infrastructure.persistence.jpa.repository.BoardEntityRepository;
import de.x1c1b.taskcare.api.infrastructure.persistence.jpa.repository.UserEntityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
@Sql(scripts = {"/sql/users.sql"})
class JpaBoardRepositoryTest {

    @Autowired
    private JpaBoardRepository jpaBoardRepository;

    @Test
    void contextLoads() {

    }

    @TestConfiguration
    @Import(BoardEntityMapperImpl.class)
    static class JpaUserRepositoryTestConfig {

        @Bean
        JpaBoardRepository jpaBoardRepository(BoardEntityRepository boardEntityRepository,
                                              UserEntityRepository userEntityRepository,
                                              BoardEntityMapper boardEntityMapper) {
            return new JpaBoardRepository(boardEntityRepository, userEntityRepository, boardEntityMapper);
        }
    }
}
