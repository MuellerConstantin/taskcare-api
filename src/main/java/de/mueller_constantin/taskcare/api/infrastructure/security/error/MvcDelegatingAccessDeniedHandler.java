package de.mueller_constantin.taskcare.api.infrastructure.security.error;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Component
@Primary
public class MvcDelegatingAccessDeniedHandler implements AccessDeniedHandler {
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Autowired
    public MvcDelegatingAccessDeniedHandler(HandlerExceptionResolver handlerExceptionResolver) {
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException exception) {
        handlerExceptionResolver.resolveException(request, response, null, exception);
    }
}
