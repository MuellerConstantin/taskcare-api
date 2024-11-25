package de.mueller_constantin.taskcare.api.infrastructure.persistence.es;

import de.mueller_constantin.taskcare.api.core.common.domain.model.Aggregate;

import java.util.Optional;
import java.util.UUID;

public interface EventStore {
    void saveAggregate(Aggregate aggregate);

    <T extends Aggregate> Optional<T> loadAggregate(UUID aggregateId, Class<T> aggregateClass, Integer version);
}
