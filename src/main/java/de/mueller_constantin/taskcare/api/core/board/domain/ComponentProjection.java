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
public class ComponentProjection implements Projection {
    private final UUID id;
    private final UUID boardId;
    private final String name;
    private final String description;

    public ComponentProjection(UUID id, UUID boardId, String name, String description) {
        this.id = id;
        this.boardId = boardId;
        this.name = name;
        this.description = description;
    }
}
