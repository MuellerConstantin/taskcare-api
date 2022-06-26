package de.x1c1b.taskcare.service.presentation.rest.v1.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
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
public class BoardDTO {

    private UUID id;
    private String name;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String description;

    private OffsetDateTime createdAt;
    private String createdBy;
}
