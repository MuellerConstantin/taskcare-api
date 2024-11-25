package de.mueller_constantin.taskcare.api.core.common.repository;

import de.mueller_constantin.taskcare.api.core.common.domain.Aggregate;

import java.util.Optional;
import java.util.UUID;

/**
 * Aggregate repository used for persisting aggregate and changes. Basically,
 * this repository is used for accessing the write model.
 *
 * @param <T> The type of aggregate.
 */
public interface AggregateRepository<T extends Aggregate> {
    void save(T aggregate);

    Optional<T> load(UUID aggregateId);

    Optional<T> load(UUID aggregateId, Integer version);
}
