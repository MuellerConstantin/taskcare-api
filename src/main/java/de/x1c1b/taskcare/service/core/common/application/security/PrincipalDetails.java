package de.x1c1b.taskcare.service.core.common.application.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Domain representation of a currently authenticated user.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PrincipalDetails {

    private String username;
    private boolean locked;
    private boolean enabled;
}
