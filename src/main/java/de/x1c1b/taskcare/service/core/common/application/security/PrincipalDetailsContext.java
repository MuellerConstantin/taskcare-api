package de.x1c1b.taskcare.service.core.common.application.security;

/**
 * Allows access to the currently authenticated principal. This allows domain
 * specific access control.
 *
 * @see PrincipalDetails
 */
@FunctionalInterface
public interface PrincipalDetailsContext {

    /**
     * Retrieves the currently authenticated principal.
     *
     * @return The authenticated principal.
     */
    PrincipalDetails getAuthenticatedPrincipal();
}
