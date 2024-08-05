package de.x1c1b.taskcare.api.presentation.rest.v1.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UpdateBoardDTO {

    private String name;
    private String description;

    @JsonIgnore
    private boolean descriptionDirty;

    public void setDescription(String description) {
        this.description = description;
        this.descriptionDirty = true;
    }
}
