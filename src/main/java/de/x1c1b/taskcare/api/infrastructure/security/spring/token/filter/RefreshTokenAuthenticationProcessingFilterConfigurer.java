package de.x1c1b.taskcare.api.infrastructure.security.spring.token.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

public class RefreshTokenAuthenticationProcessingFilterConfigurer extends AbstractHttpConfigurer<RefreshTokenAuthenticationProcessingFilterConfigurer, HttpSecurity> {

    private RequestMatcher requestMatcher;
    private AuthenticationSuccessHandler authenticationSuccessHandler;
    private AuthenticationFailureHandler authenticationFailureHandler;
    private ObjectMapper objectMapper;
    private String refreshTokenField = "refreshToken";

    @Override
    public void configure(HttpSecurity http) {
        AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
        RefreshTokenAuthenticationProcessingFilter filter = new RefreshTokenAuthenticationProcessingFilter(requestMatcher,
                authenticationSuccessHandler, authenticationFailureHandler, objectMapper,
                refreshTokenField, authenticationManager);

        http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
    }

    public RefreshTokenAuthenticationProcessingFilterConfigurer requestMatcher(RequestMatcher requestMatcher) {
        this.requestMatcher = requestMatcher;
        return this;
    }

    public RefreshTokenAuthenticationProcessingFilterConfigurer authenticationSuccessHandler(AuthenticationSuccessHandler authenticationSuccessHandler) {
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        return this;
    }

    public RefreshTokenAuthenticationProcessingFilterConfigurer authenticationFailureHandler(AuthenticationFailureHandler authenticationFailureHandler) {
        this.authenticationFailureHandler = authenticationFailureHandler;
        return this;
    }

    public RefreshTokenAuthenticationProcessingFilterConfigurer objectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        return this;
    }

    public RefreshTokenAuthenticationProcessingFilterConfigurer refreshTokenField(String refreshTokenField) {
        this.refreshTokenField = refreshTokenField;
        return this;
    }
}
