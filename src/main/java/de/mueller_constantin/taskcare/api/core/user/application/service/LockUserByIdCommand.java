package de.mueller_constantin.taskcare.api.core.user.application.service;

import de.mueller_constantin.taskcare.api.core.common.application.service.Command;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class LockUserByIdCommand implements Command {
    private UUID id;
}
