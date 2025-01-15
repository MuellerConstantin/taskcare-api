package de.mueller_constantin.taskcare.api.core.board.domain;

import de.mueller_constantin.taskcare.api.core.common.domain.Projection;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@ToString
@EqualsAndHashCode
@Builder(toBuilder = true)
public class MemberProjection implements Projection {
    private final UUID id;
    private final UUID boardId;
    private final UUID userId;
    private final Role role;

    public MemberProjection(UUID id, UUID boardId, UUID userId, Role role) {
        this.id = id;
        this.boardId = boardId;
        this.userId = userId;
        this.role = role;
    }
}
