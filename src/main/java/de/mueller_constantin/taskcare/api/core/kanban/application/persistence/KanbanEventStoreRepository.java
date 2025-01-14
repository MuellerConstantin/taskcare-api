package de.mueller_constantin.taskcare.api.core.kanban.application.persistence;

import de.mueller_constantin.taskcare.api.core.common.application.EventStoreRepository;
import de.mueller_constantin.taskcare.api.core.kanban.domain.BoardAggregate;

public interface KanbanEventStoreRepository extends EventStoreRepository<BoardAggregate> {
}
