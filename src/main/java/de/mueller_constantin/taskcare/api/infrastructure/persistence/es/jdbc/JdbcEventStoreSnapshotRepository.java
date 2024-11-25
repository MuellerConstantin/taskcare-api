package de.mueller_constantin.taskcare.api.infrastructure.persistence.es.jdbc;

import de.mueller_constantin.taskcare.api.core.common.domain.model.Aggregate;
import jakarta.annotation.Nullable;
import lombok.NonNull;

import java.util.Optional;
import java.util.UUID;

public interface JdbcEventStoreSnapshotRepository {
    void createSnapshot(@NonNull Aggregate aggregate);

    <T extends Aggregate> Optional<T> loadSnapshot(@NonNull UUID aggregateId, @Nullable Integer version);
}
