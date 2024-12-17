package de.mueller_constantin.taskcare.api.config;

import de.mueller_constantin.taskcare.api.core.common.application.persistence.MediaStorage;
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
    UserService userService(UserEventStoreRepository userEventStoreRepository,
                            UserReadModelRepository readModelRepository,
                            CredentialsEncoder credentialsEncoder,
                            MediaStorage mediaStorage,
                            Validator validator) {
        return new UserService(userEventStoreRepository, readModelRepository,
                credentialsEncoder, mediaStorage, validator);
    }
}
