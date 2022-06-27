package de.x1c1b.taskcare.service.core.board.domain;

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
public class Task {

    private UUID id;
    private String name;
    private String description;
    private Integer priority;
    private OffsetDateTime createdAt;
    private String createdBy;
    private OffsetDateTime expiresAt;
    private ProcessingStatus status;
    private String responsible;
}
