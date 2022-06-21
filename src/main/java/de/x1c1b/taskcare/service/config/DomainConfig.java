package de.x1c1b.taskcare.service.config;

import de.x1c1b.taskcare.service.core.common.application.SecretEncoder;
import de.x1c1b.taskcare.service.core.user.application.DefaultUserService;
import de.x1c1b.taskcare.service.core.user.application.UserService;
import de.x1c1b.taskcare.service.core.user.domain.UserRepository;
import de.x1c1b.taskcare.service.infrastructure.persistence.jpa.JpaUserRepository;
import de.x1c1b.taskcare.service.infrastructure.persistence.jpa.entity.mapper.UserEntityMapper;
import de.x1c1b.taskcare.service.infrastructure.persistence.jpa.repository.UserEntityRepository;
import de.x1c1b.taskcare.service.infrastructure.security.spring.SpringSecretEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DomainConfig {

    @Bean
    SecretEncoder springSecretEncoder(PasswordEncoder passwordEncoder) {
        return new SpringSecretEncoder(passwordEncoder);
    }

    @Bean
    UserRepository jpaUserRepository(UserEntityRepository userEntityRepository, UserEntityMapper userEntityMapper) {
        return new JpaUserRepository(userEntityRepository, userEntityMapper);
    }

    @Bean
    UserService defaultUserService(UserRepository userRepository, SecretEncoder secretEncoder) {
        return new DefaultUserService(userRepository, secretEncoder);
    }
}
