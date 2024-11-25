package de.mueller_constantin.taskcare.api.infrastructure.persistence.es.jdbc;

import de.mueller_constantin.taskcare.api.core.common.domain.model.Aggregate;
import lombok.NonNull;

public interface JdbcEventStoreMetadataRepository {
    void createMetadata(@NonNull Aggregate aggregate);

    void updateMetadata(@NonNull Aggregate aggregate);
}
