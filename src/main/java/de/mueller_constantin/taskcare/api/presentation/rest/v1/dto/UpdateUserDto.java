package de.mueller_constantin.taskcare.api.presentation.rest.v1.dto;

import de.mueller_constantin.taskcare.api.core.user.domain.model.Role;
import de.mueller_constantin.taskcare.api.infrastructure.validation.Enumerated;
import de.mueller_constantin.taskcare.api.infrastructure.validation.NullOrNotEmpty;
import de.mueller_constantin.taskcare.api.infrastructure.validation.Password;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UpdateUserDto {
    private String password;
    private String role;
    private String displayName;

    public Optional<@NullOrNotEmpty @Password String> getPassword() {
        return Optional.ofNullable(this.password);
    }

    public Optional<@NullOrNotEmpty @Enumerated(enumClass = Role.class) String> getRole() {
        return Optional.ofNullable(this.role);
    }

    public Optional<@NullOrNotEmpty String> getDisplayName() {
        return Optional.ofNullable(this.displayName);
    }
}
