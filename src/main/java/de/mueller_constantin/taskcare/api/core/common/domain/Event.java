package de.mueller_constantin.taskcare.api.core.common.domain;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;
import java.util.UUID;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
@EqualsAndHashCode
@SuperBuilder
public abstract class Event {
    private final UUID aggregateId;
    private final int version;
    private final OffsetDateTime timestamp = OffsetDateTime.now();
}
