package de.x1c1b.taskcare.api.infrastructure.security.spring.ticket.auth;

import de.x1c1b.taskcare.api.infrastructure.security.spring.ticket.Ticket;
import de.x1c1b.taskcare.api.infrastructure.security.spring.ticket.TicketProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TicketAuthenticationProvider implements AuthenticationProvider {

    private UserDetailsService userDetailsService;
    private TicketProvider ticketProvider;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String rawTicket = (String) authentication.getCredentials();

        Ticket ticket = ticketProvider.validateTicket(rawTicket);
        UserDetails userDetails = userDetailsService.loadUserByUsername(ticket.getPrincipal());

        return new TicketAuthenticationToken(userDetails, rawTicket, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (TicketAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
