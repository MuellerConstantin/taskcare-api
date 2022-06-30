package de.x1c1b.taskcare.service.core.user.application;

import de.x1c1b.taskcare.service.core.common.application.security.InsufficientPermissionsException;
import de.x1c1b.taskcare.service.core.common.application.security.PrincipalDetails;
import de.x1c1b.taskcare.service.core.common.application.security.PrincipalDetailsContext;
import de.x1c1b.taskcare.service.core.user.application.command.DeleteUserByUsernameCommand;
import de.x1c1b.taskcare.service.core.user.application.command.UpdateUserByUsernameCommand;
import lombok.AllArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

/**
 * Contains domain specific access and authorization checks. Access control
 * is non-invasive using AOP and ABAC.
 */
@Aspect
@AllArgsConstructor
public class UserServiceAccessInterceptor {

    private final PrincipalDetailsContext principalDetailsContext;

    @Before("execution(* de.x1c1b.taskcare.service.core.user.application.UserService.execute(de.x1c1b.taskcare.service.core.user.application.command.UpdateUserByUsernameCommand)) && args(command)")
    void evaluateUpdateUserByUsername(UpdateUserByUsernameCommand command) {

        PrincipalDetails principalDetails = principalDetailsContext.getAuthenticatedPrincipal();

        // A user can only update their own account
        if (!principalDetails.getUsername().equals(command.getUsername())) {
            throw new InsufficientPermissionsException();
        }
    }

    @Before("execution(* de.x1c1b.taskcare.service.core.user.application.UserService.execute(de.x1c1b.taskcare.service.core.user.application.command.DeleteUserByUsernameCommand)) && args(command)")
    void evaluateDeleteUserByUsername(DeleteUserByUsernameCommand command) {

        PrincipalDetails principalDetails = principalDetailsContext.getAuthenticatedPrincipal();

        // A user can only delete their own account
        if (!principalDetails.getUsername().equals(command.getUsername())) {
            throw new InsufficientPermissionsException();
        }
    }
}
