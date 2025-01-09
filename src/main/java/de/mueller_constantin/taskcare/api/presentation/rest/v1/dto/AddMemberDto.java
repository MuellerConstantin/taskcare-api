package de.mueller_constantin.taskcare.api.presentation.rest.v1.dto;

import de.mueller_constantin.taskcare.api.core.common.application.validation.Enumerated;
import de.mueller_constantin.taskcare.api.core.common.application.validation.UUID;
import de.mueller_constantin.taskcare.api.core.kanban.domain.Role;
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
public class AddMemberDto {
    @NotNull
    @NotEmpty
    @UUID
    private String userId;

    @NotNull
    @Enumerated(enumClass = Role.class)
    private String role;
}
