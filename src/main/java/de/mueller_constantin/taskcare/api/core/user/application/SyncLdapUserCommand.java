package de.mueller_constantin.taskcare.api.core.user.application;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
    private String username;

    private String displayName;
}
