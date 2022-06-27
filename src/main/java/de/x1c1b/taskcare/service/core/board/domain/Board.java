package de.x1c1b.taskcare.service.core.board.domain;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.*;

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

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Builder.Default
    private Set<Member> members = new HashSet<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Builder.Default
    private List<Task> tasks = new ArrayList<>();
}
