package de.x1c1b.taskcare.service.infrastructure.security.spring.jwt;

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
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private UserDetailsService userDetailsService;
    private JwtTokenProvider jwtTokenProvider;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String jwt = (String) authentication.getCredentials();

        jwtTokenProvider.validateToken(jwt);

        String username = jwtTokenProvider.getSubjectFromToken(jwt);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        return new JwtAuthenticationToken(userDetails, jwt, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (JwtAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
