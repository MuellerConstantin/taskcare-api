package de.mueller_constantin.taskcare.api.core.task.application.persistence;

import de.mueller_constantin.taskcare.api.core.common.application.EventStoreRepository;
import de.mueller_constantin.taskcare.api.core.task.domain.TaskAggregate;

public interface TaskEventStoreRepository extends EventStoreRepository<TaskAggregate> {
}
