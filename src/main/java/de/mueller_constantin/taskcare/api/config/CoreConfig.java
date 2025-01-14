package de.mueller_constantin.taskcare.api.config;

import de.mueller_constantin.taskcare.api.core.common.application.event.DomainEventBus;
import de.mueller_constantin.taskcare.api.core.common.application.persistence.MediaStorage;
import de.mueller_constantin.taskcare.api.core.common.application.validation.DomainValidationAspect;
import de.mueller_constantin.taskcare.api.core.kanban.application.KanbanReadService;
import de.mueller_constantin.taskcare.api.core.kanban.application.KanbanWriteService;
import de.mueller_constantin.taskcare.api.core.kanban.application.persistence.KanbanEventStoreRepository;
import de.mueller_constantin.taskcare.api.core.kanban.application.persistence.BoardReadModelRepository;
import de.mueller_constantin.taskcare.api.core.kanban.application.persistence.MemberReadModelRepository;
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
    KanbanWriteService kanbanWriteService(KanbanEventStoreRepository kanbanEventStoreRepository,
                                          BoardReadModelRepository boardReadModelRepository,
                                          UserReadService userReadService,
                                          MediaStorage mediaStorage,
                                          DomainEventBus domainEventBus) {
        return new KanbanWriteService(kanbanEventStoreRepository, boardReadModelRepository, userReadService, mediaStorage, domainEventBus);
    }

    @Bean
    KanbanReadService kanbanReadService(BoardReadModelRepository boardReadModelRepository,
                                        MemberReadModelRepository memberReadModelRepository) {
        return new KanbanReadService(boardReadModelRepository, memberReadModelRepository);
    }
}
