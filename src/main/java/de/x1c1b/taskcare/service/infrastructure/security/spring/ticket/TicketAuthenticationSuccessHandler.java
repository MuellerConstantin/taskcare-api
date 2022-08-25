package de.x1c1b.taskcare.service.infrastructure.security.spring.ticket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class TicketAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;
    private final TicketProvider ticketProvider;

    @Autowired
    public TicketAuthenticationSuccessHandler(ObjectMapper objectMapper,
                                              TicketProvider ticketProvider) {
        this.objectMapper = objectMapper;
        this.ticketProvider = ticketProvider;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        Ticket ticket = ticketProvider.generateTicket(authentication);

        Map<String, Object> tickePayload = new HashMap<>();
        tickePayload.put("principal", ticket.getPrincipal());
        tickePayload.put("ticket", ticket.getRawTicket());
        tickePayload.put("expiresIn", ticket.getExpiresIn());

        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), tickePayload);
    }
}
