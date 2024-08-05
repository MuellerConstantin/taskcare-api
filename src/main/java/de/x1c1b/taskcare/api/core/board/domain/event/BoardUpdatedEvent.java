package de.x1c1b.taskcare.api.core.board.domain.event;

import de.x1c1b.taskcare.api.core.common.application.event.DomainEvent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class BoardUpdatedEvent extends DomainEvent {

    private final UUID boardId;

    public BoardUpdatedEvent(UUID boardId, OffsetDateTime raisedAt) {
        super(String.format("board.%s.board-updated", boardId), raisedAt);
        this.boardId = boardId;
    }
}
