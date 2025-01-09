package de.mueller_constantin.taskcare.api.presentation.rest.v1.dto;

import de.mueller_constantin.taskcare.api.core.common.application.validation.Enumerated;
import de.mueller_constantin.taskcare.api.core.common.application.validation.NullOrNotEmpty;
import de.mueller_constantin.taskcare.api.core.kanban.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UpdateMemberDto {
    private String role;

    public Optional<@NullOrNotEmpty @Enumerated(enumClass = Role.class) String> getRole() {
        return Optional.ofNullable(this.role);
    }
}
