package de.mueller_constantin.taskcare.api.core.user.application;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SyncLdapUserCommand {
    @NotNull
    @NotEmpty
    @Size(max = 255)
    private String username;

    @Size(max = 255)
    private String displayName;
}
