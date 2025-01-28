package de.mueller_constantin.taskcare.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.mueller_constantin.taskcare.api.core.common.application.persistence.MediaStorage;
import de.mueller_constantin.taskcare.api.core.user.application.UserReadService;
import de.mueller_constantin.taskcare.api.core.user.application.UserWriteService;
import de.mueller_constantin.taskcare.api.infrastructure.security.ldap.LdapSecurityProperties;
import de.mueller_constantin.taskcare.api.infrastructure.security.ldap.LdapUserContextMapper;
import de.mueller_constantin.taskcare.api.infrastructure.security.ldap.LdapUserMapper;
import de.mueller_constantin.taskcare.api.infrastructure.security.token.RefreshToken;
import de.mueller_constantin.taskcare.api.infrastructure.security.ajax.AjaxAuthenticationProcessingFilterConfigurer;
import de.mueller_constantin.taskcare.api.infrastructure.security.token.AccessToken;
import de.mueller_constantin.taskcare.api.infrastructure.security.token.TokenProvider;
import de.mueller_constantin.taskcare.api.infrastructure.security.token.auth.AccessTokenAuthenticationProvider;
import de.mueller_constantin.taskcare.api.infrastructure.security.token.auth.RefreshTokenAuthenticationProvider;
import de.mueller_constantin.taskcare.api.infrastructure.security.token.filter.AccessTokenAuthenticationFilterConfigurer;
import de.mueller_constantin.taskcare.api.infrastructure.security.token.filter.RefreshTokenAuthenticationProcessingFilterConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.authentication.LdapAuthenticator;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.security.ldap.userdetails.DefaultLdapAuthoritiesPopulator;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private AccessDeniedHandler accessDeniedHandler;

    @Autowired
    private AuthenticationFailureHandler authenticationFailureHandler;

    @Autowired
    private AuthenticationSuccessHandler authenticationSuccessHandler;

    @Autowired
    private TokenProvider<AccessToken> accessTokenProvider;

    @Autowired
    private TokenProvider<RefreshToken> refreshTokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    PasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity httpSecurity, List<AuthenticationProvider> authenticationProviders) throws Exception {
        sessionCreationPolicy(httpSecurity);
        exceptionHandling(httpSecurity);
        csrf(httpSecurity);
        cors(httpSecurity);
        authenticateRequests(httpSecurity, authenticationProviders);
        authorizeRequests(httpSecurity);

        httpSecurity.with(new AjaxAuthenticationProcessingFilterConfigurer(),
                        (dsl) -> dsl.requestMatcher(
                                new AntPathRequestMatcher("/api/v1/auth/token", HttpMethod.POST.name()))
                                .objectMapper(objectMapper)
                                .authenticationFailureHandler(authenticationFailureHandler)
                                .authenticationSuccessHandler(authenticationSuccessHandler))
                .with(new RefreshTokenAuthenticationProcessingFilterConfigurer(),
                        (dsl) -> dsl.requestMatcher(
                                new AntPathRequestMatcher("/api/v1/auth/refresh", HttpMethod.POST.name()))
                                .authenticationFailureHandler(authenticationFailureHandler)
                                .authenticationSuccessHandler(authenticationSuccessHandler)
                                .objectMapper(objectMapper))
                .with(new AccessTokenAuthenticationFilterConfigurer(), (dsl) -> {});

        return httpSecurity.build();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    DaoAuthenticationProvider daoAuthenticationProvider(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(bCryptPasswordEncoder());

        return daoAuthenticationProvider;
    }

    @Bean
    AccessTokenAuthenticationProvider accessTokenAuthenticationProvider(UserDetailsService userDetailsService) {
        AccessTokenAuthenticationProvider accessTokenAuthenticationProvider = new AccessTokenAuthenticationProvider();
        accessTokenAuthenticationProvider.setUserDetailsService(userDetailsService);
        accessTokenAuthenticationProvider.setAccessTokenProvider(accessTokenProvider);

        return accessTokenAuthenticationProvider;
    }

    @Bean
    RefreshTokenAuthenticationProvider refreshTokenAuthenticationProvider(UserDetailsService userDetailsService) {
        RefreshTokenAuthenticationProvider refreshTokenAuthenticationProvider = new RefreshTokenAuthenticationProvider();
        refreshTokenAuthenticationProvider.setUserDetailsService(userDetailsService);
        refreshTokenAuthenticationProvider.setRefreshTokenProvider(refreshTokenProvider);

        return refreshTokenAuthenticationProvider;
    }

    protected void sessionCreationPolicy(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.sessionManagement((sessionManagementConfigurer) -> sessionManagementConfigurer
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
    }

    protected void exceptionHandling(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.exceptionHandling((exceptionHandlingConfigurer) -> exceptionHandlingConfigurer
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler));
    }

    protected void csrf(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(AbstractHttpConfigurer::disable);
    }

    protected void cors(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.cors((corsConfigurer) -> corsConfigurer.configurationSource(request -> {
            CorsConfiguration configuration = new CorsConfiguration();
            configuration.setAllowedOrigins(List.of("*"));
            configuration.setAllowedMethods(List.of("*"));
            configuration.setAllowedHeaders(List.of("*"));

            return configuration;
        }));
    }

    protected void authenticateRequests(HttpSecurity httpSecurity, List<AuthenticationProvider> authenticationProviders) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = httpSecurity.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationProviders.forEach(authenticationManagerBuilder::authenticationProvider);
        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();

        httpSecurity.authenticationManager(authenticationManager);
    }

    protected void authorizeRequests(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests((authorizeRequests) -> authorizeRequests
                .requestMatchers(HttpMethod.POST, "/api/v1/auth/token", "/api/v1/auth/refresh")
                .permitAll()
                .anyRequest().authenticated());
    }

    @Configuration
    @ConditionalOnProperty(prefix = "taskcare.security.ldap", name = "enabled", havingValue = "true")
    public static class LdapConfig {
        @Autowired
        private LdapSecurityProperties ldapSecurityProperties;

        @Bean
        LdapAuthenticationProvider ldapAuthenticationProvider(LdapUserContextMapper ldapUserContextMapper) {
            LdapAuthenticationProvider ldapAuthenticationProvider = new LdapAuthenticationProvider(authenticator(), authoritiesPopulator());
            ldapAuthenticationProvider.setUserDetailsContextMapper(ldapUserContextMapper);

            return ldapAuthenticationProvider;
        }

        @Bean
        LdapAuthoritiesPopulator authoritiesPopulator() {
            return new DefaultLdapAuthoritiesPopulator(ldapContextSource(), null);
        }

        @Bean
        FilterBasedLdapUserSearch ldapUserSearch() {
            final String searchBase = ldapSecurityProperties.getUserSearchBase();
            final String searchFilter = ldapSecurityProperties.getUserSearchFilter();

            return new FilterBasedLdapUserSearch(searchBase, searchFilter, ldapContextSource());
        }

        @Bean
        LdapUserContextMapper ldapUserContextMapper(UserWriteService userWriteService,
                                                    UserReadService userReadService,
                                                    MediaStorage mediaStorage,
                                                    LdapUserMapper ldapUserMapper) {
            return new LdapUserContextMapper(userWriteService, userReadService, mediaStorage, ldapUserMapper);
        }

        @Bean
        LdapAuthenticator authenticator() {
            BindAuthenticator authenticator = new BindAuthenticator(ldapContextSource());
            authenticator.setUserSearch(ldapUserSearch());

            return authenticator;
        }

        @Bean
        LdapUserMapper ldapUserMapper() {
            return new LdapUserMapper(ldapSecurityProperties);
        }

        @Bean
        LdapContextSource ldapContextSource() {
            final LdapContextSource ldapContextSource = new LdapContextSource();
            ldapContextSource.setUserDn(ldapSecurityProperties.getManagerDn());
            ldapContextSource.setPassword(ldapSecurityProperties.getManagerPassword());
            ldapContextSource.setBase(ldapSecurityProperties.getBase());
            ldapContextSource.setUrl(ldapSecurityProperties.getUrl());

            return ldapContextSource;
        }
    }
}
