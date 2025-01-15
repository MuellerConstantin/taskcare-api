package de.mueller_constantin.taskcare.api.config;

import de.mueller_constantin.taskcare.api.core.board.application.persistence.*;
import de.mueller_constantin.taskcare.api.core.common.application.event.DomainEventBus;
import de.mueller_constantin.taskcare.api.core.common.application.persistence.MediaStorage;
import de.mueller_constantin.taskcare.api.core.common.application.validation.DomainValidationAspect;
import de.mueller_constantin.taskcare.api.core.board.application.BoardReadService;
import de.mueller_constantin.taskcare.api.core.board.application.BoardWriteService;
import de.mueller_constantin.taskcare.api.core.user.application.UserReadService;
import de.mueller_constantin.taskcare.api.core.user.application.UserWriteService;
import de.mueller_constantin.taskcare.api.core.user.application.persistence.UserEventStoreRepository;
import de.mueller_constantin.taskcare.api.core.user.application.persistence.UserReadModelRepository;
import de.mueller_constantin.taskcare.api.core.user.application.security.CredentialsEncoder;
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
    UserWriteService userWriteService(UserEventStoreRepository userEventStoreRepository,
                                      UserReadModelRepository readModelRepository,
                                      CredentialsEncoder credentialsEncoder,
                                      MediaStorage mediaStorage,
                                      DomainEventBus domainEventBus) {
        return new UserWriteService(userEventStoreRepository, readModelRepository,
                credentialsEncoder, mediaStorage, domainEventBus);
    }

    @Bean
    UserReadService userReadService(UserReadModelRepository readModelRepository) {
        return new UserReadService(readModelRepository);
    }

    @Bean
    BoardWriteService kanbanWriteService(BoardEventStoreRepository boardEventStoreRepository,
                                         BoardReadModelRepository boardReadModelRepository,
                                         UserReadService userReadService,
                                         MediaStorage mediaStorage,
                                         DomainEventBus domainEventBus) {
        return new BoardWriteService(boardEventStoreRepository, boardReadModelRepository, userReadService, mediaStorage, domainEventBus);
    }

    @Bean
    BoardReadService kanbanReadService(BoardReadModelRepository boardReadModelRepository,
                                       MemberReadModelRepository memberReadModelRepository,
                                       StatusReadModelRepository statusReadModelRepository,
                                       ComponentReadModelRepository componentReadModelRepository) {
        return new BoardReadService(boardReadModelRepository,
                memberReadModelRepository, statusReadModelRepository, componentReadModelRepository);
    }
}
