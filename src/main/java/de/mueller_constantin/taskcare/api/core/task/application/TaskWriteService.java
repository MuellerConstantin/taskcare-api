package de.mueller_constantin.taskcare.api.core.task.application;

import de.mueller_constantin.taskcare.api.core.board.application.BoardReadService;
import de.mueller_constantin.taskcare.api.core.board.application.query.ExistsBoardByIdQuery;
import de.mueller_constantin.taskcare.api.core.board.application.query.ExistsComponentByComponentIdAndBoardIdQuery;
import de.mueller_constantin.taskcare.api.core.board.application.query.ExistsMemberByMemberIdAndBoardIdQuery;
import de.mueller_constantin.taskcare.api.core.board.application.query.ExistsStatusByStatusIdAndBoardIdQuery;
import de.mueller_constantin.taskcare.api.core.board.domain.BoardDeletedEvent;
import de.mueller_constantin.taskcare.api.core.board.domain.ComponentRemovedEvent;
import de.mueller_constantin.taskcare.api.core.board.domain.MemberRemovedEvent;
import de.mueller_constantin.taskcare.api.core.board.domain.StatusRemovedEvent;
import de.mueller_constantin.taskcare.api.core.common.application.ApplicationService;
import de.mueller_constantin.taskcare.api.core.common.application.NoSuchEntityException;
import de.mueller_constantin.taskcare.api.core.common.application.event.DomainEventBus;
import de.mueller_constantin.taskcare.api.core.common.domain.DomainEvent;
import de.mueller_constantin.taskcare.api.core.task.application.command.CreateTaskCommand;
import de.mueller_constantin.taskcare.api.core.task.application.command.DeleteTaskByIdCommand;
import de.mueller_constantin.taskcare.api.core.task.application.command.UpdateTaskByIdCommand;
import de.mueller_constantin.taskcare.api.core.task.application.persistence.TaskEventStoreRepository;
import de.mueller_constantin.taskcare.api.core.task.application.persistence.TaskReadModelRepository;
import de.mueller_constantin.taskcare.api.core.task.domain.Priority;
import de.mueller_constantin.taskcare.api.core.task.domain.TaskAggregate;
import jakarta.validation.Valid;
import lombok.SneakyThrows;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static lombok.Lombok.sneakyThrow;

@Service
@Validated
@Transactional
@Retryable(
        retryFor = OptimisticLockingFailureException.class,
        maxAttempts = 3,
        backoff = @Backoff(delay = 100, multiplier = 2)
)
public class TaskWriteService implements ApplicationService {
    private final TaskEventStoreRepository taskEventStoreRepository;
    private final TaskReadModelRepository taskReadModelRepository;
    private final BoardReadService boardReadService;
    private final DomainEventBus domainEventBus;
    private final LockRegistry lockRegistry;

    public TaskWriteService(TaskEventStoreRepository taskEventStoreRepository,
                            TaskReadModelRepository taskReadModelRepository,
                            BoardReadService boardReadService,
                            DomainEventBus domainEventBus,
                            LockRegistry lockRegistry) {
        this.taskEventStoreRepository = taskEventStoreRepository;
        this.taskReadModelRepository = taskReadModelRepository;
        this.boardReadService = boardReadService;
        this.domainEventBus = domainEventBus;
        this.lockRegistry = lockRegistry;

        this.domainEventBus.subscribe(BoardDeletedEvent.class, this::onBoardDeletedEvent);
        this.domainEventBus.subscribe(StatusRemovedEvent.class, this::onStatusRemovedEvent);
        this.domainEventBus.subscribe(ComponentRemovedEvent.class, this::onComponentRemovedEvent);
        this.domainEventBus.subscribe(MemberRemovedEvent.class, this::onMemberRemovedEvent);
    }

    public void dispatch(@Valid CreateTaskCommand command) {
        if(!boardReadService.query(new ExistsBoardByIdQuery(command.getBoardId()))) {
            throw new NoSuchEntityException("Board with id '" + command.getBoardId() + "' does not exist");
        }

        if(command.getAssigneeId() != null && !boardReadService.query(ExistsMemberByMemberIdAndBoardIdQuery.builder()
                .boardId(command.getBoardId())
                .memberId(command.getAssigneeId())
                .build())) {
            throw new NoSuchEntityException("Member with id '" + command.getAssigneeId() + "' does not exist");
        }

        if(command.getStatusId() != null && !boardReadService.query(ExistsStatusByStatusIdAndBoardIdQuery.builder()
                .boardId(command.getBoardId())
                .statusId(command.getStatusId())
                .build())) {
            throw new NoSuchEntityException("Status with id '" + command.getStatusId() + "' does not exist");
        }

        if(command.getComponentIds() != null) {
            command.getComponentIds().forEach(componentId -> {
                if(!boardReadService.query(ExistsComponentByComponentIdAndBoardIdQuery.builder()
                        .boardId(command.getBoardId())
                        .componentId(componentId)
                        .build())) {
                    throw new NoSuchEntityException("Component with id '" + componentId + "' does not exist");
                }
            });
        }

        TaskAggregate taskAggregate = new TaskAggregate();
        taskAggregate.create(command.getBoardId(),
                command.getName(),
                command.getDescription(),
                command.getAssigneeId(),
                command.getStatusId(),
                command.getComponentIds(),
                command.getDueDate(),
                command.getEstimatedEffort(),
                command.getPriority());
        taskEventStoreRepository.save(taskAggregate);
    }

    @SneakyThrows
    public void dispatch(@Valid UpdateTaskByIdCommand command) {
        lockRegistry.executeLocked(command.getId().toString(), () -> {
            TaskAggregate taskAggregate = taskEventStoreRepository.load(command.getId())
                    .orElseThrow(NoSuchEntityException::new);

            if(command.getAssigneeId() != null && !boardReadService.query(ExistsMemberByMemberIdAndBoardIdQuery.builder()
                    .boardId(taskAggregate.getBoardId())
                    .memberId(command.getAssigneeId())
                    .build())) {
                throw new NoSuchEntityException("Member with id '" + command.getAssigneeId() + "' does not exist");
            }

            if(command.getStatusId() != null && !boardReadService.query(ExistsStatusByStatusIdAndBoardIdQuery.builder()
                    .boardId(taskAggregate.getBoardId())
                    .statusId(command.getStatusId())
                    .build())) {
                throw new NoSuchEntityException("Status with id '" + command.getStatusId() + "' does not exist");
            }

            if(command.getComponentIds() != null) {
                command.getComponentIds().forEach(componentId -> {
                    if(!boardReadService.query(ExistsComponentByComponentIdAndBoardIdQuery.builder()
                            .boardId(taskAggregate.getBoardId())
                            .componentId(componentId)
                            .build())) {
                        throw new NoSuchEntityException("Component with id '" + componentId + "' does not exist");
                    }
                });
            }

            String name = command.getName() != null ?
                    command.getName() :
                    taskAggregate.getName();

            String description = command.isDescriptionTouched() ?
                    command.getDescription() :
                    taskAggregate.getDescription();

            UUID assigneeId = command.isAssigneeIdTouched() ?
                    command.getAssigneeId() :
                    taskAggregate.getAssigneeId();

            UUID statusId = command.isStatusIdTouched() ?
                    command.getStatusId() :
                    taskAggregate.getStatusId();

            Set<UUID> componentIds = command.getComponentIds() != null ?
                    command.getComponentIds() :
                    taskAggregate.getComponentIds();

            OffsetDateTime dueDate = command.isDueDateTouched() ?
                    command.getDueDate() :
                    taskAggregate.getDueDate();

            Long estimatedEffort = command.isEstimatedEffortTouched() ?
                    command.getEstimatedEffort() :
                    taskAggregate.getEstimatedEffort();

            Priority priority = command.isPriorityTouched() ?
                    command.getPriority() :
                    taskAggregate.getPriority();

            taskAggregate.update(name,
                    description,
                    assigneeId,
                    statusId,
                    componentIds,
                    dueDate,
                    estimatedEffort,
                    priority);
            taskEventStoreRepository.save(taskAggregate);
        });
    }

    @SneakyThrows
    public void dispatch(@Valid DeleteTaskByIdCommand command) {
        lockRegistry.executeLocked(command.getId().toString(), () -> {
            TaskAggregate taskAggregate = taskEventStoreRepository.load(command.getId())
                    .orElseThrow(NoSuchEntityException::new);
            taskAggregate.delete();
            taskEventStoreRepository.save(taskAggregate);
        });
    }

    protected void onBoardDeletedEvent(DomainEvent event) {
        BoardDeletedEvent boardDeletedEvent = (BoardDeletedEvent) event;

        List<UUID> taskIds = taskReadModelRepository.findAllIdsByBoardId(boardDeletedEvent.getAggregateId());

        taskIds.parallelStream().forEach(taskId -> {
            try {
                lockRegistry.executeLocked(taskId.toString(), () -> {
                    TaskAggregate taskAggregate = taskEventStoreRepository.load(taskId)
                            .orElseThrow(NoSuchEntityException::new);
                    taskAggregate.delete();
                    taskEventStoreRepository.save(taskAggregate);
                });
            } catch (InterruptedException exc) {
                throw sneakyThrow(exc);
            }
        });
    }

    protected void onStatusRemovedEvent(DomainEvent event) {
        StatusRemovedEvent statusRemovedEvent = (StatusRemovedEvent) event;

        List<UUID> taskIds = taskReadModelRepository.findAllIdsByBoardIdAndStatusId(
                statusRemovedEvent.getAggregateId(), statusRemovedEvent.getStatus().getId());

        taskIds.parallelStream().forEach(taskId -> {
            try {
                lockRegistry.executeLocked(taskId.toString(), () -> {
                    TaskAggregate taskAggregate = taskEventStoreRepository.load(taskId)
                            .orElseThrow(NoSuchEntityException::new);

                    taskAggregate.update(taskAggregate.getName(),
                            taskAggregate.getDescription(),
                            taskAggregate.getAssigneeId(),
                            null,
                            taskAggregate.getComponentIds(),
                            taskAggregate.getDueDate(),
                            taskAggregate.getEstimatedEffort(),
                            taskAggregate.getPriority());

                    taskEventStoreRepository.save(taskAggregate);
                });
            } catch (InterruptedException exc) {
                throw sneakyThrow(exc);
            }
        });
    }

    protected void onComponentRemovedEvent(DomainEvent event) {
        ComponentRemovedEvent componentRemovedEvent = (ComponentRemovedEvent) event;

        List<UUID> taskIds = taskReadModelRepository.findAllIdsByBoardIdAndComponentId(
                componentRemovedEvent.getAggregateId(), componentRemovedEvent.getComponent().getId());

        taskIds.parallelStream().forEach(taskId -> {
            try {
                lockRegistry.executeLocked(taskId.toString(), () -> {
                    TaskAggregate taskAggregate = taskEventStoreRepository.load(taskId)
                            .orElseThrow(NoSuchEntityException::new);

                    taskAggregate.update(taskAggregate.getName(),
                            taskAggregate.getDescription(),
                            taskAggregate.getAssigneeId(),
                            taskAggregate.getStatusId(),
                            taskAggregate.getComponentIds().stream()
                                    .filter(componentId -> !componentId.equals(componentRemovedEvent.getComponent().getId()))
                                    .collect(Collectors.toSet()),
                            taskAggregate.getDueDate(),
                            taskAggregate.getEstimatedEffort(),
                            taskAggregate.getPriority());

                    taskEventStoreRepository.save(taskAggregate);
                });
            } catch (InterruptedException exc) {
                throw sneakyThrow(exc);
            }
        });
    }

    protected void onMemberRemovedEvent(DomainEvent event) {
        MemberRemovedEvent memberRemovedEvent = (MemberRemovedEvent) event;

        List<UUID> taskIds = taskReadModelRepository.findAllIdsByBoardIdAndAssigneeId(
                memberRemovedEvent.getAggregateId(), memberRemovedEvent.getMember().getId());

        taskIds.parallelStream().forEach(taskId -> {
            try {
                lockRegistry.executeLocked(taskId.toString(), () -> {
                    TaskAggregate taskAggregate = taskEventStoreRepository.load(taskId)
                            .orElseThrow(NoSuchEntityException::new);

                    taskAggregate.update(taskAggregate.getName(),
                            taskAggregate.getDescription(),
                            null,
                            taskAggregate.getStatusId(),
                            taskAggregate.getComponentIds(),
                            taskAggregate.getDueDate(),
                            taskAggregate.getEstimatedEffort(),
                            taskAggregate.getPriority());

                    taskEventStoreRepository.save(taskAggregate);
                });
            } catch (InterruptedException exc) {
                throw sneakyThrow(exc);
            }
        });
    }
}
