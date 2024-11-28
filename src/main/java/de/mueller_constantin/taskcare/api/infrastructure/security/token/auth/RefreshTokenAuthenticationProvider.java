package de.mueller_constantin.taskcare.api.infrastructure.security.token.auth;

import de.mueller_constantin.taskcare.api.infrastructure.security.token.RefreshToken;
import de.mueller_constantin.taskcare.api.infrastructure.security.token.TokenProvider;
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
