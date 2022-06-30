package de.x1c1b.taskcare.service.infrastructure.security.spring.token.filter;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class AccessTokenAuthenticationFilterConfigurer extends AbstractHttpConfigurer<AccessTokenAuthenticationFilterConfigurer, HttpSecurity> {

    @Override
    public void configure(HttpSecurity http) {
        AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
        AccessTokenAuthenticationFilter filter = new AccessTokenAuthenticationFilter(authenticationManager);

        http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
    }
}
