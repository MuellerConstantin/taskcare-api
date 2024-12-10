package de.mueller_constantin.taskcare.api.config;

import de.mueller_constantin.taskcare.api.core.common.application.persistence.MediaStorage;
import de.mueller_constantin.taskcare.api.core.user.application.persistence.UserDomainRepository;
import de.mueller_constantin.taskcare.api.core.user.application.persistence.UserStateRepository;
import de.mueller_constantin.taskcare.api.core.user.application.security.CredentialsEncoder;
import de.mueller_constantin.taskcare.api.core.user.application.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CoreConfig {
    @Bean
    UserService userService(UserDomainRepository userAggregateRepository,
                            UserStateRepository userProjectionRepository,
                            CredentialsEncoder credentialsEncoder,
                            MediaStorage mediaStorage) {
        return new UserService(userAggregateRepository, userProjectionRepository, credentialsEncoder, mediaStorage);
    }
}
