package de.mueller_constantin.taskcare.api.core.kanban.domain;

import de.mueller_constantin.taskcare.api.core.common.domain.Projection;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.Set;
import java.util.UUID;

@Getter
@ToString
@EqualsAndHashCode
@SuperBuilder
public class BoardProjection implements Projection {
    private final UUID id;
    private final String name;
    private final String description;
    private final Set<MemberProjection> members;
}
