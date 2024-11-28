package de.mueller_constantin.taskcare.api.presentation.rest.v1.dto;

import de.mueller_constantin.taskcare.api.core.user.domain.model.Role;
import de.mueller_constantin.taskcare.api.infrastructure.validation.Enumerated;
import de.mueller_constantin.taskcare.api.infrastructure.validation.NullOrNotEmpty;
import de.mueller_constantin.taskcare.api.infrastructure.validation.Password;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CreateUserDto {
    @NotNull
    @NotEmpty
    private String username;

    @NotNull
    @Password
    private String password;

    @NotNull
    @Enumerated(enumClass = Role.class)
    private String role;

    private String displayName;

    public Optional<@NullOrNotEmpty String> getDisplayName() {
        return Optional.ofNullable(this.displayName);
    }
}
