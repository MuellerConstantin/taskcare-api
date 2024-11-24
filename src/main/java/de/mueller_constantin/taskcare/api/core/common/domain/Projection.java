package de.mueller_constantin.taskcare.api.core.common.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

/**
 * Base class for projections.
 */
@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
@SuperBuilder
public abstract class Projection {
    private final UUID id;
}
