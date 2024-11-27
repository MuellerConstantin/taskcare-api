package de.mueller_constantin.taskcare.api.infrastructure.security.token;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@Primary
public class AccessTokenAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final ObjectMapper objectMapper;
    private final TokenProvider<AccessToken> accessTokenTokenProvider;

    @Autowired
    public AccessTokenAuthenticationSuccessHandler(ObjectMapper objectMapper,
                                             TokenProvider<AccessToken> accessTokenTokenProvider) {
        this.objectMapper = objectMapper;
        this.accessTokenTokenProvider = accessTokenTokenProvider;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        AccessToken accessToken = accessTokenTokenProvider.generateToken(authentication);

        Map<String, Object> token = new HashMap<>();
        token.put("type", "Bearer");
        token.put("principal", accessToken.getPrincipal());
        token.put("accessToken", accessToken.getRawToken());
        token.put("accessExpiresIn", accessToken.getExpiresIn());

        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), token);
    }
}
