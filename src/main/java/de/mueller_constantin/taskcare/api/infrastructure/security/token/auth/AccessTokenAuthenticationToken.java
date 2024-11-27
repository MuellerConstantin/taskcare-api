package de.mueller_constantin.taskcare.api.infrastructure.security.token.auth;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class AccessTokenAuthenticationToken extends AbstractAuthenticationToken {
    private final String rawToken;
    private final UserDetails userDetails;

    public AccessTokenAuthenticationToken(String rawToken) {
        super(null);
        this.rawToken = rawToken;
        this.userDetails = null;
        this.setAuthenticated(false);
    }

    public AccessTokenAuthenticationToken(UserDetails userDetails, String rawToken, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.rawToken = rawToken;
        this.userDetails = userDetails;
        super.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return rawToken;
    }

    @Override
    public Object getPrincipal() {
        return userDetails;
    }
}
