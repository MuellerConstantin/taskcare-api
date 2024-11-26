package de.mueller_constantin.taskcare.api.config;

import de.mueller_constantin.taskcare.api.core.user.application.repository.UserAggregateRepository;
import de.mueller_constantin.taskcare.api.core.user.application.repository.UserProjectionRepository;
import de.mueller_constantin.taskcare.api.core.user.application.security.CredentialsEncoder;
import de.mueller_constantin.taskcare.api.core.user.application.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CoreConfig {
    @Bean
    UserService userService(UserAggregateRepository userAggregateRepository,
                            UserProjectionRepository userProjectionRepository,
                            CredentialsEncoder credentialsEncoder) {
        return new UserService(userAggregateRepository, userProjectionRepository, credentialsEncoder);
    }
}
