package de.x1c1b.taskcare.service.infrastructure.security.spring.ticket.filter;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class TicketAuthenticationFilterConfigurer extends AbstractHttpConfigurer<TicketAuthenticationFilterConfigurer, HttpSecurity> {

    @Override
    public void configure(HttpSecurity http) {
        AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
        TicketAuthenticationFilter filter = new TicketAuthenticationFilter(authenticationManager);

        http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
    }
}
