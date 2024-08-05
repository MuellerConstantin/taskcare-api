package de.x1c1b.taskcare.api.infrastructure.security.spring.token.auth;

import de.x1c1b.taskcare.api.infrastructure.security.spring.token.RefreshToken;
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
public class RefreshTokenAuthenticationProvider implements AuthenticationProvider {

    private UserDetailsService userDetailsService;
    private TokenProvider<RefreshToken> refreshTokenProvider;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String rawToken = (String) authentication.getCredentials();

        RefreshToken refreshToken = refreshTokenProvider.validateToken(rawToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(refreshToken.getPrincipal());

        return new RefreshTokenAuthenticationToken(userDetails, rawToken, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (RefreshTokenAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
