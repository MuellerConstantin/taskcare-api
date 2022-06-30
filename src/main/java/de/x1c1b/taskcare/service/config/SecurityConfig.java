package de.x1c1b.taskcare.service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.x1c1b.taskcare.service.infrastructure.security.spring.ajax.AjaxAuthenticationProcessingFilterConfigurer;
import de.x1c1b.taskcare.service.infrastructure.security.spring.jwt.JwtAuthenticationFilterConfigurer;
import de.x1c1b.taskcare.service.infrastructure.security.spring.jwt.JwtAuthenticationProvider;
import de.x1c1b.taskcare.service.infrastructure.security.spring.jwt.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
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
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           AuthenticationEntryPoint authenticationEntryPoint,
                                           AccessDeniedHandler accessDeniedHandler,
                                           AuthenticationFailureHandler authenticationFailureHandler,
                                           AuthenticationSuccessHandler authenticationSuccessHandler,
                                           ObjectMapper objectMapper,
                                           UserDetailsService userDetailsService,
                                           PasswordEncoder passwordEncoder,
                                           JwtTokenProvider jwtTokenProvider) throws Exception {

        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);

        JwtAuthenticationProvider jwtAuthenticationProvider = new JwtAuthenticationProvider();
        jwtAuthenticationProvider.setUserDetailsService(userDetailsService);
        jwtAuthenticationProvider.setJwtTokenProvider(jwtTokenProvider);

        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(daoAuthenticationProvider);
        authenticationManagerBuilder.authenticationProvider(jwtAuthenticationProvider);

        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();

        http.authenticationManager(authenticationManager)
                .cors()
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
                .authenticated()
                .and()
                .apply(new JwtAuthenticationFilterConfigurer())
                .and()
                .apply(new AjaxAuthenticationProcessingFilterConfigurer())
                .requestMatcher(new AntPathRequestMatcher("/v1/auth/token", HttpMethod.POST.name()))
                .authenticationFailureHandler(authenticationFailureHandler)
                .authenticationSuccessHandler(authenticationSuccessHandler)
                .objectMapper(objectMapper);

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
}
