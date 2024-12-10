package de.mueller_constantin.taskcare.api.infrastructure.security;

import de.mueller_constantin.taskcare.api.core.user.domain.UserProjection;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

@RequiredArgsConstructor
@Data
@Builder
public class Principal implements UserDetails {
    private final UserProjection userProjection;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Set.of(new SimpleGrantedAuthority("ROLE_" + userProjection.getRole().name()));
    }

    @Override
    public String getPassword() {
        return userProjection.getPassword();
    }

    @Override
    public String getUsername() {
        return userProjection.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !userProjection.isLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
