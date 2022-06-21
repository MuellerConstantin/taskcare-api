package de.x1c1b.taskcare.service.infrastructure.persistence.jpa;

import de.x1c1b.taskcare.service.infrastructure.persistence.jpa.mapper.UserEntityMapper;
import de.x1c1b.taskcare.service.infrastructure.persistence.jpa.mapper.UserEntityMapperImpl;
import de.x1c1b.taskcare.service.infrastructure.persistence.jpa.repository.UserEntityRepository;
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
class JpaUserRepositoryTest {

    @Autowired
    private JpaUserRepository jpaUserRepository;

    @Test
    void contextLoads() {

    }

    @TestConfiguration
    @Import(UserEntityMapperImpl.class)
    static class JpaUserRepositoryTestConfig {

        @Bean
        JpaUserRepository jpaUserRepository(UserEntityRepository userEntityRepository, UserEntityMapper userEntityMapper) {
            return new JpaUserRepository(userEntityRepository, userEntityMapper);
        }
    }
}
