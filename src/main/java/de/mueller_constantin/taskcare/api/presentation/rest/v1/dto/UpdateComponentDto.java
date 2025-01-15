package de.mueller_constantin.taskcare.api.presentation.rest.v1.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.mueller_constantin.taskcare.api.core.common.application.validation.NullOrNotEmpty;
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
public class UpdateComponentDto {
    private String name;
    private String description;

    @JsonIgnore
    private boolean descriptionTouched;

    public void setDescription(String description) {
        this.description = description;
        this.descriptionTouched = true;
    }

    public Optional<@NullOrNotEmpty @Size(max = 255) String> getName() {
        return Optional.ofNullable(this.name);
    }

    public Optional<@NullOrNotEmpty @Size(max = 1024) String> getDescription() {
        return Optional.ofNullable(this.description);
    }
}
