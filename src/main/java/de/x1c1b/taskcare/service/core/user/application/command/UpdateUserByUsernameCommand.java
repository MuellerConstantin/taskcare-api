package de.x1c1b.taskcare.service.core.user.application.command;

import de.x1c1b.taskcare.service.core.common.application.validation.NullOrNotEmpty;
import de.x1c1b.taskcare.service.core.common.application.validation.Password;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.util.Optional;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UpdateUserByUsernameCommand {

    private String username;
    private String email;
    private String password;
    private String firstName;
    private String lastName;

    private boolean firstNameDirty;
    private boolean lastNameDirty;

    public Optional<@NullOrNotEmpty @Size(max = 1024) @Email String> getEmail() {
        return Optional.ofNullable(email);
    }

    public Optional<@NullOrNotEmpty @Size(max = 256) @Password String> getPassword() {
        return Optional.ofNullable(password);
    }

    public Optional<@NullOrNotEmpty @Size(max = 100) String> getFirstName() {
        return Optional.ofNullable(firstName);
    }

    public Optional<@NullOrNotEmpty @Size(max = 100) String> getLastName() {
        return Optional.ofNullable(lastName);
    }
}
