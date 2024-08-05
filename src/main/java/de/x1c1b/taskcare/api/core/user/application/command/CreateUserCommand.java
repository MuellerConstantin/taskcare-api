package de.x1c1b.taskcare.api.core.user.application.command;

import de.x1c1b.taskcare.api.core.common.application.validation.NullOrNotEmpty;
import de.x1c1b.taskcare.api.core.common.application.validation.Password;
import de.x1c1b.taskcare.api.core.common.application.validation.Username;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Optional;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CreateUserCommand {
    @NotNull
    @NotEmpty
    @Username
    @Size(max = 15)
    private String username;

    @NotNull
    @NotEmpty
    @Email
    @Size(max = 1024)
    private String email;

    @NotEmpty
    @NotNull
    @Password
    @Size(max = 256)
    private String password;

    private String firstName;
    private String lastName;

    public Optional<@NullOrNotEmpty @Size(max = 100) String> getFirstName() {
        return Optional.ofNullable(firstName);
    }

    public Optional<@NullOrNotEmpty @Size(max = 100) String> getLastName() {
        return Optional.ofNullable(lastName);
    }
}
