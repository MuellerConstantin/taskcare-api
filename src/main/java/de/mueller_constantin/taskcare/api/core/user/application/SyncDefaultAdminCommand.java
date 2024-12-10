package de.mueller_constantin.taskcare.api.core.user.application;

import de.mueller_constantin.taskcare.api.core.common.application.Command;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SyncDefaultAdminCommand implements Command {
    private String password;
}
