package de.x1c1b.taskcare.api.infrastructure.security.spring.ticket.filter;

import de.x1c1b.taskcare.api.infrastructure.security.spring.ticket.auth.TicketAuthenticationToken;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@AllArgsConstructor
public class TicketAuthenticationFilter extends OncePerRequestFilter {
    private final AuthenticationManager authenticationManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String ticket = parseTicket(request);

        if (null != ticket) {

            Authentication authentication = authenticationManager.authenticate(new TicketAuthenticationToken(ticket));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String parseTicket(HttpServletRequest request) {

        String accessTokenParameter = request.getParameter("ticket");

        if (StringUtils.hasText(accessTokenParameter)) {
            return accessTokenParameter;
        }

        return null;
    }
}
