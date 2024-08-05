package de.x1c1b.taskcare.api.infrastructure.security.spring.error;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Primary
public class MvcDelegatingAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final HandlerExceptionResolver handlerExceptionResolver;

    @Autowired
    public MvcDelegatingAuthenticationFailureHandler(HandlerExceptionResolver handlerExceptionResolver) {
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) {
        handlerExceptionResolver.resolveException(request, response, null, exception);
    }
}
