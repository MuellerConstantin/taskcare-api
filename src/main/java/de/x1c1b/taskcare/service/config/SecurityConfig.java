package de.x1c1b.taskcare.service.config;

import de.x1c1b.taskcare.service.infrastructure.security.spring.error.MvcDelegatingAccessDeniedHandler;
import de.x1c1b.taskcare.service.infrastructure.security.spring.error.MvcDelegatingAuthenticationEntryPoint;
import de.x1c1b.taskcare.service.infrastructure.security.spring.jwt.JwtAuthenticationFilter;
import de.x1c1b.taskcare.service.infrastructure.security.spring.jwt.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           JwtAuthenticationFilter jwtAuthenticationFilter,
                                           AuthenticationEntryPoint authenticationEntryPoint,
                                           AccessDeniedHandler accessDeniedHandler) throws Exception {

        http.cors()
                .and()
                .csrf()
                .disable()
                .httpBasic()
                .disable()
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/v1/users")
                .permitAll()
                .antMatchers(HttpMethod.POST, "/v1/auth/token")
                .permitAll()
                .anyRequest()
                .authenticated();

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    PasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    @Primary
    AuthenticationEntryPoint mvcDelegatingAuthenticationEntryPoint(HandlerExceptionResolver handlerExceptionResolver) {
        return new MvcDelegatingAuthenticationEntryPoint(handlerExceptionResolver);
    }

    @Bean
    @Primary
    AccessDeniedHandler mvcDelegatingAccessDeniedHandler(HandlerExceptionResolver handlerExceptionResolver) {
        return new MvcDelegatingAccessDeniedHandler(handlerExceptionResolver);
    }

    @Bean
    JwtAuthenticationFilter jwtAuthenticationFilter(UserDetailsService userDetailsService, JwtTokenProvider jwtTokenProvider) {
        return new JwtAuthenticationFilter(userDetailsService, jwtTokenProvider);
    }
}
