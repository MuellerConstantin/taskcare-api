package de.mueller_constantin.taskcare.api.config;

import de.mueller_constantin.taskcare.api.core.common.application.event.DomainEventBus;
import de.mueller_constantin.taskcare.api.core.common.application.persistence.MediaStorage;
import de.mueller_constantin.taskcare.api.core.common.application.validation.DomainValidationAspect;
import de.mueller_constantin.taskcare.api.core.kanban.application.BoardService;
import de.mueller_constantin.taskcare.api.core.kanban.application.persistence.BoardEventStoreRepository;
import de.mueller_constantin.taskcare.api.core.kanban.application.persistence.BoardReadModelRepository;
import de.mueller_constantin.taskcare.api.core.user.application.persistence.UserEventStoreRepository;
import de.mueller_constantin.taskcare.api.core.user.application.persistence.UserReadModelRepository;
import de.mueller_constantin.taskcare.api.core.user.application.security.CredentialsEncoder;
import de.mueller_constantin.taskcare.api.core.user.application.UserService;
import jakarta.validation.Validator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CoreConfig {
    @Bean
    DomainValidationAspect domainValidationAspect(Validator validator) {
        return new DomainValidationAspect(validator);
    }

    @Bean
    UserService userService(UserEventStoreRepository userEventStoreRepository,
                            UserReadModelRepository readModelRepository,
                            CredentialsEncoder credentialsEncoder,
                            MediaStorage mediaStorage,
                            DomainEventBus domainEventBus) {
        return new UserService(userEventStoreRepository, readModelRepository,
                credentialsEncoder, mediaStorage, domainEventBus);
    }

    @Bean
    BoardService boardService(BoardEventStoreRepository boardEventStoreRepository,
                              BoardReadModelRepository boardReadModelRepository,
                              UserService userService,
                              DomainEventBus domainEventBus) {
        return new BoardService(boardEventStoreRepository, boardReadModelRepository, userService, domainEventBus);
    }
}
