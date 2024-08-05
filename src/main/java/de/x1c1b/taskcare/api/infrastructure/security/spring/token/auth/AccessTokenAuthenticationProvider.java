package de.x1c1b.taskcare.api.infrastructure.security.spring.token.auth;

import de.x1c1b.taskcare.api.infrastructure.security.spring.token.AccessToken;
import de.x1c1b.taskcare.api.infrastructure.security.spring.token.TokenProvider;
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
