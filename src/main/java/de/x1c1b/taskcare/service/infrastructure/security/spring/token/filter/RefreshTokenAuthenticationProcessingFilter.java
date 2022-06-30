package de.x1c1b.taskcare.service.infrastructure.security.spring.token.filter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.x1c1b.taskcare.service.infrastructure.security.spring.token.auth.RefreshTokenAuthenticationToken;
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

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class RefreshTokenAuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {
    private static final Logger logger = LoggerFactory.getLogger(RefreshTokenAuthenticationProcessingFilter.class);

    private final AuthenticationSuccessHandler authenticationSuccessHandler;
    private final AuthenticationFailureHandler authenticationFailureHandler;
    private final ObjectMapper objectMapper;
    private final String refreshTokenField;

    public RefreshTokenAuthenticationProcessingFilter(RequestMatcher requestMatcher,
                                                      AuthenticationSuccessHandler authenticationSuccessHandler,
                                                      AuthenticationFailureHandler authenticationFailureHandler,
                                                      ObjectMapper objectMapper,
                                                      String refreshTokenField,
                                                      AuthenticationManager authenticationManager) {
        super(requestMatcher, authenticationManager);
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        this.authenticationFailureHandler = authenticationFailureHandler;
        this.objectMapper = objectMapper;
        this.refreshTokenField = refreshTokenField;
    }

    public RefreshTokenAuthenticationProcessingFilter(RequestMatcher requestMatcher,
                                                      AuthenticationSuccessHandler authenticationSuccessHandler,
                                                      AuthenticationFailureHandler authenticationFailureHandler,
                                                      ObjectMapper objectMapper,
                                                      AuthenticationManager authenticationManager) {
        this(requestMatcher, authenticationSuccessHandler, authenticationFailureHandler,
                objectMapper, "refreshToken", authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException {

        if (!HttpMethod.POST.name().equals(request.getMethod())) {
            logger.debug("Authentication HTTP method not supported. Request HTTP method: " + request.getMethod());
            throw new AuthenticationServiceException("Authentication method not supported");
        }

        Map<String, Object> body = objectMapper.readValue(request.getReader(), new TypeReference<>() {
        });

        if (null == body.get(refreshTokenField) || body.get(refreshTokenField).toString().isBlank()) {
            throw new AuthenticationServiceException("Refresh token not provided");
        }

        RefreshTokenAuthenticationToken token = new RefreshTokenAuthenticationToken((String) body.get(refreshTokenField));

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
