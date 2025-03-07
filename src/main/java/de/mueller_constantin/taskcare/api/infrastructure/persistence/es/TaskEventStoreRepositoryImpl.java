package de.mueller_constantin.taskcare.api.infrastructure.persistence.es;

import de.mueller_constantin.taskcare.api.core.task.application.persistence.TaskEventStoreRepository;
import de.mueller_constantin.taskcare.api.core.task.domain.TaskAggregate;
import de.mueller_constantin.taskcare.api.core.task.domain.TaskProjection;
import de.mueller_constantin.taskcare.api.infrastructure.persistence.crud.TaskCrudRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Component
@Transactional
@RequiredArgsConstructor
public class TaskEventStoreRepositoryImpl implements TaskEventStoreRepository {
    private final EventStore eventStore;
    private final TaskCrudRepository taskCrudRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void save(TaskAggregate aggregate) {
        eventStore.saveAggregate(aggregate);

        // Synchronize read model with event store

        if(aggregate.isDeleted()) {
            taskCrudRepository.deleteById(aggregate.getId());
        } else {
            TaskProjection projection = TaskProjection.builder()
                    .id(aggregate.getId())
                    .boardId(aggregate.getBoardId())
                    .name(aggregate.getName())
                    .description(aggregate.getDescription())
                    .statusId(aggregate.getStatusId())
                    .statusUpdatedAt(aggregate.getStatusUpdatedAt())
                    .updatedAt(aggregate.getUpdatedAt())
                    .assigneeId(aggregate.getAssigneeId())
                    .componentIds(aggregate.getComponentIds())
                    .priority(aggregate.getPriority())
                    .dueDate(aggregate.getDueDate())
                    .createdAt(aggregate.getCreatedAt())
                    .build();

            taskCrudRepository.save(projection);
        }

        aggregate.getUncommittedEvents().forEach(applicationEventPublisher::publishEvent);
        aggregate.commit();
    }

    @Override
    public Optional<TaskAggregate> load(UUID aggregateId) {
        return eventStore.loadAggregate(aggregateId, TaskAggregate.class, null);
    }

    @Override
    public Optional<TaskAggregate> load(UUID aggregateId, Integer version) {
        return eventStore.loadAggregate(aggregateId, TaskAggregate.class, version);
    }
}
