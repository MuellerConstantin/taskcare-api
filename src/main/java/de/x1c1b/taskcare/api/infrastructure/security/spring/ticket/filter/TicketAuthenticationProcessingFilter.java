package de.x1c1b.taskcare.api.infrastructure.security.spring.ticket.filter;

import de.x1c1b.taskcare.api.infrastructure.security.spring.token.auth.AccessTokenAuthenticationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TicketAuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {
    private static final Logger logger = LoggerFactory.getLogger(TicketAuthenticationProcessingFilter.class);

    private final AuthenticationSuccessHandler authenticationSuccessHandler;
    private final AuthenticationFailureHandler authenticationFailureHandler;

    public TicketAuthenticationProcessingFilter(RequestMatcher requestMatcher,
                                                AuthenticationSuccessHandler authenticationSuccessHandler,
                                                AuthenticationFailureHandler authenticationFailureHandler,
                                                AuthenticationManager authenticationManager) {
        super(requestMatcher, authenticationManager);
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        this.authenticationFailureHandler = authenticationFailureHandler;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        if (!HttpMethod.POST.name().equals(request.getMethod())) {
            logger.debug("Authentication HTTP method not supported. Request HTTP method: " + request.getMethod());
            throw new AuthenticationServiceException("Authentication method not supported");
        }

        String authorizationHeader = request.getHeader("Authorization");

        if (!StringUtils.hasText(authorizationHeader) || !authorizationHeader.startsWith("Bearer ")) {
            throw new AuthenticationServiceException("Access token not provided");
        }

        AccessTokenAuthenticationToken token = new AccessTokenAuthenticationToken(authorizationHeader.substring("Bearer ".length()));

        return this.getAuthenticationManager().authenticate(token);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        authenticationSuccessHandler.onAuthenticationSuccess(request, response, authResult);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
        SecurityContextHolder.clearContext();
        authenticationFailureHandler.onAuthenticationFailure(request, response, failed);
    }
}
