package de.mueller_constantin.taskcare.api.core.common.application;

import de.mueller_constantin.taskcare.api.core.common.domain.Aggregate;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for accessing the domain (write) model. This repository allows to
 * load any revision of a domain entity (aggregate).
 *
 * @param <T> The type of aggregate.
 */
public interface EventStoreRepository<T extends Aggregate> {
    /**
     * Persists the aggregate as a chain of events.
     *
     * @param aggregate The aggregate to persist.
     */
    void save(T aggregate);

    /**
     * Loads the latest version of the aggregate.
     *
     * @param aggregateId The id of the aggregate.
     * @return The aggregate.
     */
    Optional<T> load(UUID aggregateId);

    /**
     * Reconstructs the aggregate from a specific version.
     *
     * <p>
     *     If the aggregate is not available in the specified version,
     *     the next smaller version, i.e. the most recent version of the
     *     aggregate, should be loaded.
     * </p>
     *
     * @param aggregateId The id of the aggregate.
     * @param version The version of the aggregate.
     * @return The aggregate.
     */
    Optional<T> load(UUID aggregateId, Integer version);
}
