package de.mueller_constantin.taskcare.api.core.board.domain;

import de.mueller_constantin.taskcare.api.core.common.domain.DomainEvent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class MemberAddedEvent extends DomainEvent {
    private final Member member;

    public MemberAddedEvent() {
        this(UUID.randomUUID(), 0, null);
    }

    public MemberAddedEvent(UUID aggregateId, int version, Member member) {
        super(aggregateId, version);
        this.member = member;
    }
}
