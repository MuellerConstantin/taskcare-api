package de.x1c1b.taskcare.service.infrastructure.security.spring.jwt;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final String jwt;
    private final UserDetails userDetails;

    public JwtAuthenticationToken(String jwt) {
        super(null);
        this.jwt = jwt;
        this.userDetails = null;
        this.setAuthenticated(false);
    }

    public JwtAuthenticationToken(UserDetails userDetails, String jwt, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.jwt = jwt;
        this.userDetails = userDetails;
        super.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return jwt;
    }

    @Override
    public Object getPrincipal() {
        return userDetails;
    }
}
