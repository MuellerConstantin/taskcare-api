package de.mueller_constantin.taskcare.api.infrastructure.security.ajax;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

public class AjaxAuthenticationProcessingFilterConfigurer extends AbstractHttpConfigurer<AjaxAuthenticationProcessingFilterConfigurer, HttpSecurity> {
    private RequestMatcher requestMatcher;
    private AuthenticationSuccessHandler authenticationSuccessHandler;
    private AuthenticationFailureHandler authenticationFailureHandler;
    private ObjectMapper objectMapper;
    private String usernameField = "username";
    private String passwordField = "password";

    @Override
    public void configure(HttpSecurity http) {
        AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
        AjaxAuthenticationProcessingFilter filter = new AjaxAuthenticationProcessingFilter(requestMatcher,
                authenticationSuccessHandler, authenticationFailureHandler, objectMapper,
                usernameField, passwordField, authenticationManager);

        http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
    }

    public AjaxAuthenticationProcessingFilterConfigurer requestMatcher(RequestMatcher requestMatcher) {
        this.requestMatcher = requestMatcher;
        return this;
    }

    public AjaxAuthenticationProcessingFilterConfigurer authenticationSuccessHandler(AuthenticationSuccessHandler authenticationSuccessHandler) {
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        return this;
    }

    public AjaxAuthenticationProcessingFilterConfigurer authenticationFailureHandler(AuthenticationFailureHandler authenticationFailureHandler) {
        this.authenticationFailureHandler = authenticationFailureHandler;
        return this;
    }

    public AjaxAuthenticationProcessingFilterConfigurer objectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        return this;
    }

    public AjaxAuthenticationProcessingFilterConfigurer usernameField(String usernameField) {
        this.usernameField = usernameField;
        return this;
    }

    public AjaxAuthenticationProcessingFilterConfigurer passwordField(String passwordField) {
        this.passwordField = passwordField;
        return this;
    }
}
