package de.mueller_constantin.taskcare.api.core.kanban.domain;

import de.mueller_constantin.taskcare.api.core.common.domain.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter
@Setter(value = AccessLevel.PACKAGE)
@ToString(callSuper = true)
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class Member extends Entity {
    private UUID boardId;
    private UUID userId;
    private Role role;

    public Member(UUID id, UUID boardId, UUID userId, Role role) {
        super(id);
        this.boardId = boardId;
        this.userId = userId;
        this.role = role;
    }
}
