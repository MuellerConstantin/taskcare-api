package de.x1c1b.taskcare.service.infrastructure.security.spring.ticket.auth;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class TicketAuthenticationToken extends AbstractAuthenticationToken {

    private final String rawTicket;
    private final UserDetails userDetails;

    public TicketAuthenticationToken(String rawTicket) {
        super(null);
        this.rawTicket = rawTicket;
        this.userDetails = null;
        this.setAuthenticated(false);
    }

    public TicketAuthenticationToken(UserDetails userDetails, String rawTicket, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.rawTicket = rawTicket;
        this.userDetails = userDetails;
        super.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return rawTicket;
    }

    @Override
    public Object getPrincipal() {
        return userDetails;
    }
}
