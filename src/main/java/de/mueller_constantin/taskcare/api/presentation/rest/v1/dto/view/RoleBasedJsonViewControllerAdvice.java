package de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.view;

import de.mueller_constantin.taskcare.api.core.user.domain.Role;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.AbstractMappingJacksonResponseBodyAdvice;

import java.util.List;

@RestControllerAdvice
public class RoleBasedJsonViewControllerAdvice extends AbstractMappingJacksonResponseBodyAdvice {
    @Override
    protected void beforeBodyWriteInternal(MappingJacksonValue bodyContainer, MediaType contentType, MethodParameter returnType, ServerHttpRequest request, ServerHttpResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication != null && authentication.isAuthenticated()) {
            List<Role> roles = authentication.getAuthorities().stream()
                    .filter(grantedAuthority -> grantedAuthority.getAuthority().startsWith("ROLE_"))
                    .map(grantedAuthority -> grantedAuthority.getAuthority().substring(5))
                    .map(Role::valueOf)
                    .toList();

            if(roles.size() == 1) {
                switch(roles.get(0)) {
                    case ADMINISTRATOR -> bodyContainer.setSerializationView(DefaultJsonViews.Administrator.class);
                    case USER -> bodyContainer.setSerializationView(DefaultJsonViews.User.class);
                    default -> throw new IllegalArgumentException("Unknown role for @JsonView serialization");
                }

                return;
            }

            throw new IllegalArgumentException("Ambiguous roles for @JsonView serialization");
        }
    }
}
