package de.x1c1b.taskcare.api.config;

import de.x1c1b.taskcare.api.core.board.application.BoardService;
import de.x1c1b.taskcare.api.core.board.application.BoardServiceAccessInterceptor;
import de.x1c1b.taskcare.api.core.board.application.DefaultBoardService;
import de.x1c1b.taskcare.api.core.board.domain.BoardRepository;
import de.x1c1b.taskcare.api.core.common.application.event.DomainEventPublisher;
import de.x1c1b.taskcare.api.core.common.application.security.PrincipalDetailsContext;
import de.x1c1b.taskcare.api.core.common.application.security.SecretEncoder;
import de.x1c1b.taskcare.api.core.user.application.DefaultUserService;
import de.x1c1b.taskcare.api.core.user.application.UserService;
import de.x1c1b.taskcare.api.core.user.application.UserServiceAccessInterceptor;
import de.x1c1b.taskcare.api.core.user.domain.UserRepository;
import de.x1c1b.taskcare.api.infrastructure.event.simp.SimpDomainEventPublisher;
import de.x1c1b.taskcare.api.infrastructure.persistence.jpa.JpaBoardRepository;
import de.x1c1b.taskcare.api.infrastructure.persistence.jpa.JpaUserRepository;
import de.x1c1b.taskcare.api.infrastructure.persistence.jpa.entity.mapper.BoardEntityMapper;
import de.x1c1b.taskcare.api.infrastructure.persistence.jpa.entity.mapper.UserEntityMapper;
import de.x1c1b.taskcare.api.infrastructure.persistence.jpa.repository.BoardEntityRepository;
import de.x1c1b.taskcare.api.infrastructure.persistence.jpa.repository.UserEntityRepository;
import de.x1c1b.taskcare.api.infrastructure.security.spring.SpringPrincipalDetailsContext;
import de.x1c1b.taskcare.api.infrastructure.security.spring.SpringSecretEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DomainConfig {

    @Bean
    SecretEncoder springSecretEncoder(PasswordEncoder passwordEncoder) {
        return new SpringSecretEncoder(passwordEncoder);
    }

    @Bean
    PrincipalDetailsContext springPrincipalDetailsContext() {
        return new SpringPrincipalDetailsContext();
    }

    @Bean
    DomainEventPublisher simpDomainEventPublisher(SimpMessagingTemplate simpMessagingTemplate) {
        return new SimpDomainEventPublisher(simpMessagingTemplate);
    }

    @Bean
    UserRepository jpaUserRepository(UserEntityRepository userEntityRepository, UserEntityMapper userEntityMapper) {
        return new JpaUserRepository(userEntityRepository, userEntityMapper);
    }

    @Bean
    UserService defaultUserService(UserRepository userRepository, SecretEncoder secretEncoder) {
        return new DefaultUserService(userRepository, secretEncoder);
    }

    @Bean
    UserServiceAccessInterceptor userServiceAccessInterceptor(PrincipalDetailsContext principalDetailsContext) {
        return new UserServiceAccessInterceptor(principalDetailsContext);
    }

    @Bean
    BoardRepository jpaBoardRepository(BoardEntityRepository boardEntityRepository,
                                       UserEntityRepository userEntityRepository,
                                       BoardEntityMapper boardEntityMapper) {
        return new JpaBoardRepository(boardEntityRepository, userEntityRepository, boardEntityMapper);
    }

    @Bean
    BoardService defaultBoardService(BoardRepository boardRepository, DomainEventPublisher domainEventPublisher) {
        return new DefaultBoardService(boardRepository, domainEventPublisher);
    }

    @Bean
    BoardServiceAccessInterceptor boardServiceAccessInterceptor(PrincipalDetailsContext principalDetailsContext,
                                                                BoardRepository boardRepository) {
        return new BoardServiceAccessInterceptor(principalDetailsContext, boardRepository);
    }
}
