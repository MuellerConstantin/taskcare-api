package de.x1c1b.taskcare.service.infrastructure.security.spring.ticket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Ticket {

    private String rawTicket;
    private long expiresIn;
    private String principal;
}
