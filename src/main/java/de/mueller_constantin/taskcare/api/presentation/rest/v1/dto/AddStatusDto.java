package de.mueller_constantin.taskcare.api.presentation.rest.v1.dto;

import de.mueller_constantin.taskcare.api.core.common.application.validation.NullOrNotEmpty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AddStatusDto {
    @NotNull
    @NotEmpty
    @Size(max = 255)
    private String name;

    private String description;

    public Optional<@NullOrNotEmpty @Size(max = 1024) String> getDescription() { return Optional.ofNullable(this.description); }
}
