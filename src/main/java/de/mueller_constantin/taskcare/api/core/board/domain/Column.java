package de.mueller_constantin.taskcare.api.core.board.domain;

import de.mueller_constantin.taskcare.api.core.common.domain.ValueObject;
import lombok.*;

import java.util.UUID;

@Getter
@Setter(value = AccessLevel.PACKAGE)
@ToString
@Builder
@EqualsAndHashCode
public class Column implements ValueObject {
    private UUID statusId;
}
