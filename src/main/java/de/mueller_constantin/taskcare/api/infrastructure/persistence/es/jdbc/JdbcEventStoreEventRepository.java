package de.mueller_constantin.taskcare.api.infrastructure.persistence.es.jdbc;

import de.mueller_constantin.taskcare.api.core.common.domain.DomainEvent;
import jakarta.annotation.Nullable;
import lombok.NonNull;

import java.util.List;
import java.util.UUID;

public interface JdbcEventStoreEventRepository {
    void createEvent(@NonNull DomainEvent event);

    List<DomainEvent> loadEvents(@NonNull UUID aggregateId, @Nullable Integer fromVersion, @Nullable Integer toVersion);
}
