package de.x1c1b.taskcare.api.infrastructure.security.spring.ticket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

@Component
public class TicketProvider {

    private final TicketProperties ticketProperties;
    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public TicketProvider(TicketProperties ticketProperties, RedisTemplate<String, Object> redisTemplate) {
        this.ticketProperties = ticketProperties;
        this.redisTemplate = redisTemplate;
    }

    public Ticket generateTicket(Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        SecureRandom secureRandom = new SecureRandom();
        byte[] secret = new byte[ticketProperties.getLength()];
        secureRandom.nextBytes(secret);

        String rawTicket = Base64.getEncoder().encodeToString(secret);
        String storageKey = String.format("ticket:%s", rawTicket);

        redisTemplate.opsForValue().set(storageKey, user.getUsername(),
                ticketProperties.getExpiresIn(), TimeUnit.MILLISECONDS);

        return Ticket.builder()
                .rawTicket(rawTicket)
                .expiresIn(ticketProperties.getExpiresIn())
                .principal(user.getUsername())
                .build();
    }

    public Ticket validateTicket(String rawTicket) throws InvalidTicketException {
        String storageKey = String.format("ticket:%s", rawTicket);

        if (Boolean.TRUE.equals(redisTemplate.hasKey(storageKey))) {
            String principal = (String) redisTemplate.opsForValue().get(storageKey);

            // Ensures that a ticket can only be used once
            redisTemplate.delete(storageKey);

            return Ticket.builder()
                    .rawTicket(rawTicket)
                    .expiresIn(-1)
                    .principal(principal)
                    .build();
        } else {
            throw new InvalidTicketException("Invalid ticket");
        }
    }
}
