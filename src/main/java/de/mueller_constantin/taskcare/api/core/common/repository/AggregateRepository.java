package de.mueller_constantin.taskcare.api.core.common.repository;

import de.mueller_constantin.taskcare.api.core.common.domain.Aggregate;

import java.util.Optional;
import java.util.UUID;

public interface AggregateRepository<T extends Aggregate> {
    void save(T aggregate);

    Optional<T> load(UUID aggregateId);

    Optional<T> load(UUID aggregateId, Integer version);
}
