package de.mueller_constantin.taskcare.api.infrastructure.persistence.es.jdbc;

import de.mueller_constantin.taskcare.api.core.common.domain.Aggregate;
import jakarta.annotation.Nullable;
import lombok.NonNull;

import java.util.List;
import java.util.UUID;

public interface JdbcEventStoreMetadataRepository {
    void createMetadata(@NonNull Aggregate aggregate);

    void updateMetadata(@NonNull Aggregate aggregate);
}
