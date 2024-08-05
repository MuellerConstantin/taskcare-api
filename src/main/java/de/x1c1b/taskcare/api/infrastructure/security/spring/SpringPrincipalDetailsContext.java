package de.x1c1b.taskcare.api.infrastructure.security.spring;

import de.x1c1b.taskcare.api.core.common.application.security.PrincipalDetails;
import de.x1c1b.taskcare.api.core.common.application.security.PrincipalDetailsContext;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class SpringPrincipalDetailsContext implements PrincipalDetailsContext {

    @Override
    public PrincipalDetails getAuthenticatedPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (null == authentication || !authentication.isAuthenticated()) {
            throw new InsufficientAuthenticationException("User must be authenticated");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        return PrincipalDetails.builder()
                .username(userDetails.getUsername())
                .enabled(userDetails.isEnabled())
                .locked(!userDetails.isAccountNonLocked())
                .build();
    }
}
