package de.mueller_constantin.taskcare.api.core.kanban.domain;

import de.mueller_constantin.taskcare.api.core.common.domain.Projection;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter
@ToString
@EqualsAndHashCode
@SuperBuilder
public class MemberProjection implements Projection {
    private final UUID id;
    private final UUID userId;
    private final Role role;

    public MemberProjection(UUID id, UUID userId, Role role) {
        this.id = id;
        this.userId = userId;
        this.role = role;
    }
}
