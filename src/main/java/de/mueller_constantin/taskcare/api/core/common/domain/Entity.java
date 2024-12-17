package de.mueller_constantin.taskcare.api.core.common.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

/**
 * Base class for entities.
 */
@Getter
@ToString
@EqualsAndHashCode
@SuperBuilder
public abstract class Entity {
    private final UUID id;

    public Entity(UUID id) {
        this.id = id;
    }
}
