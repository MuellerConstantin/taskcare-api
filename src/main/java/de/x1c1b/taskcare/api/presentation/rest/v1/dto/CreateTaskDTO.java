package de.x1c1b.taskcare.api.presentation.rest.v1.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CreateTaskDTO {

    private String name;
    private String description;
    private OffsetDateTime expiresAt;
    private Integer priority;
    private String responsible;
}
