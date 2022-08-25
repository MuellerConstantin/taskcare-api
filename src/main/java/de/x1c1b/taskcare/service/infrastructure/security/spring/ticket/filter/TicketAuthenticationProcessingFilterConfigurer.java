package de.x1c1b.taskcare.service.infrastructure.security.spring.ticket.filter;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

public class TicketAuthenticationProcessingFilterConfigurer extends AbstractHttpConfigurer<TicketAuthenticationProcessingFilterConfigurer, HttpSecurity> {

    private RequestMatcher requestMatcher;
    private AuthenticationSuccessHandler authenticationSuccessHandler;
    private AuthenticationFailureHandler authenticationFailureHandler;

    @Override
    public void configure(HttpSecurity http) {
        AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
        TicketAuthenticationProcessingFilter filter = new TicketAuthenticationProcessingFilter(requestMatcher,
                authenticationSuccessHandler, authenticationFailureHandler, authenticationManager);

        http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
    }

    public TicketAuthenticationProcessingFilterConfigurer requestMatcher(RequestMatcher requestMatcher) {
        this.requestMatcher = requestMatcher;
        return this;
    }

    public TicketAuthenticationProcessingFilterConfigurer authenticationSuccessHandler(AuthenticationSuccessHandler authenticationSuccessHandler) {
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        return this;
    }

    public TicketAuthenticationProcessingFilterConfigurer authenticationFailureHandler(AuthenticationFailureHandler authenticationFailureHandler) {
        this.authenticationFailureHandler = authenticationFailureHandler;
        return this;
    }
}
