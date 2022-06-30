package de.x1c1b.taskcare.service.presentation.rest.v1.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UpdateTaskDTO {

    private String name;
    private String description;
    private OffsetDateTime expiresAt;
    private Integer priority;
    private String responsible;
    private String status;

    @JsonIgnore
    private boolean expiresAtDirty;

    @JsonIgnore
    private boolean priorityDirty;

    @JsonIgnore
    private boolean responsibleDirty;

    @JsonIgnore
    private boolean descriptionDirty;

    public void setDescription(String description) {
        this.description = description;
        this.descriptionDirty = true;
    }

    public void setExpiresAt(OffsetDateTime expiresAt) {
        this.expiresAt = expiresAt;
        this.expiresAtDirty = true;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
        this.priorityDirty = true;
    }

    public void setResponsible(String responsible) {
        this.responsible = responsible;
        this.responsibleDirty = true;
    }
}
