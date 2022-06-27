package de.x1c1b.taskcare.service.presentation.rest.v1.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import de.x1c1b.taskcare.service.core.board.domain.ProcessingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TaskDTO {

    private UUID id;
    private String name;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String description;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer priority;

    private OffsetDateTime createdAt;
    private String createdBy;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private OffsetDateTime expiresAt;

    private ProcessingStatus status;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String responsible;
}
