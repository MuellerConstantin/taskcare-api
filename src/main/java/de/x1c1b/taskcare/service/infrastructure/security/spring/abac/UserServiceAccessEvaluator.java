package de.x1c1b.taskcare.service.infrastructure.security.spring.abac;

import de.x1c1b.taskcare.service.core.user.application.command.DeleteUserByUsernameCommand;
import de.x1c1b.taskcare.service.core.user.application.command.UpdateUserByUsernameCommand;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class UserServiceAccessEvaluator {

    private static UserDetails extractCurrentPrinciple() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (null == authentication || !authentication.isAuthenticated()) {
            throw new InsufficientAuthenticationException("User must be authenticated");
        }

        return (UserDetails) authentication.getPrincipal();
    }

    @Before("execution(* de.x1c1b.taskcare.service.core.user.application.UserService.execute(de.x1c1b.taskcare.service.core.user.application.command.UpdateUserByUsernameCommand)) && args(command)")
    void evaluateUpdateUserByUsername(UpdateUserByUsernameCommand command) {

        UserDetails currentPrincipal = extractCurrentPrinciple();

        // A user can only update their own account
        if (!currentPrincipal.getUsername().equals(command.getUsername())) {
            throw new AccessDeniedException("Permissions are missing for access");
        }
    }

    @Before("execution(* de.x1c1b.taskcare.service.core.user.application.UserService.execute(de.x1c1b.taskcare.service.core.user.application.command.DeleteUserByUsernameCommand)) && args(command)")
    void evaluateDeleteUserByUsername(DeleteUserByUsernameCommand command) {

        UserDetails currentPrincipal = extractCurrentPrinciple();

        // A user can only delete their own account
        if (!currentPrincipal.getUsername().equals(command.getUsername())) {
            throw new AccessDeniedException("Permissions are missing for access");
        }
    }
}
