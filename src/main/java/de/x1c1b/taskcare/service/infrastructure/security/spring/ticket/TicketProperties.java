package de.x1c1b.taskcare.service.infrastructure.security.spring.ticket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "taskcare.security.ticket")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TicketProperties {

    @Builder.Default
    private int length = 8;

    @Builder.Default
    private long expiresIn = 60000L; // 1 minute
}
