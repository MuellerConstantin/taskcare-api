package de.x1c1b.taskcare.service.core.board.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Board {

    private UUID id;
    private String name;
    private String description;
    private OffsetDateTime createdAt;
    private String createdBy;

    @Builder.Default
    private Set<Member> members = new HashSet<>();
}
