package de.mueller_constantin.taskcare.api.infrastructure.automation;

import de.mueller_constantin.taskcare.api.core.user.application.command.SyncDefaultAdminCommand;
import de.mueller_constantin.taskcare.api.core.user.application.UserWriteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "taskcare.automation.default-admin-synchronization", name = "enabled", havingValue = "true", matchIfMissing = true)
@Slf4j
public class DefaultAdminSynchronizationTask implements ApplicationListener<ApplicationStartedEvent> {
    private final UserWriteService userWriteService;

    private final String password;

    @Autowired
    public DefaultAdminSynchronizationTask(UserWriteService userWriteService,
                                           @Value("${taskcare.automation.default-admin-synchronization.password:#{null}}") String password) {
        this.userWriteService = userWriteService;
        this.password = password;
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        if(this.password == null) {
            log.warn("Default admin password is not set. Skipping default admin user synchronization...");
            return;
        }

        log.info("Syncing default admin user...");

        this.userWriteService.dispatch(SyncDefaultAdminCommand.builder()
                .password(this.password)
                .build());
    }
}
