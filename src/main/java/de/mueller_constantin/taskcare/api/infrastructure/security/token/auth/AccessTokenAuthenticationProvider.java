package de.mueller_constantin.taskcare.api.infrastructure.security.token.auth;

import de.mueller_constantin.taskcare.api.infrastructure.security.token.AccessToken;
import de.mueller_constantin.taskcare.api.infrastructure.security.token.TokenProvider;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

@NoArgsConstructor
@Setter
public class AccessTokenAuthenticationProvider implements AuthenticationProvider {
    private UserDetailsService userDetailsService;
    private TokenProvider<AccessToken> accessTokenProvider;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String rawToken = (String) authentication.getCredentials();

        AccessToken accessToken = accessTokenProvider.validateToken(rawToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(accessToken.getPrincipal());

        return new AccessTokenAuthenticationToken(userDetails, rawToken, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (AccessTokenAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
