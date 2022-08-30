package de.x1c1b.taskcare.service.core.board.domain.event;

import de.x1c1b.taskcare.service.core.common.application.event.DomainEvent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class BoardCreatedEvent extends DomainEvent {

    private final UUID boardId;

    public BoardCreatedEvent(UUID boardId, OffsetDateTime raisedAt) {
        super("board.board-created", raisedAt);
        this.boardId = boardId;
    }
}
