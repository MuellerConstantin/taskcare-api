package de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.mapper;

import de.mueller_constantin.taskcare.api.core.common.domain.Page;
import de.mueller_constantin.taskcare.api.core.task.application.command.CreateTaskCommand;
import de.mueller_constantin.taskcare.api.core.task.application.command.UpdateTaskByIdCommand;
import de.mueller_constantin.taskcare.api.core.task.domain.Priority;
import de.mueller_constantin.taskcare.api.core.task.domain.TaskProjection;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface TaskDtoMapper {
    TaskDto mapToDto(TaskProjection taskProjection);

    @Mapping(source = "createTaskDto.description", target = "description", qualifiedByName = "unwrapOptional")
    @Mapping(source = "createTaskDto.statusId", target = "statusId", qualifiedByName = "unwrapOptional")
    @Mapping(source = "createTaskDto.assigneeId", target = "assigneeId", qualifiedByName = "unwrapOptional")
    @Mapping(source = "createTaskDto.componentIds", target = "componentIds", qualifiedByName = "unwrapOptionalComponentIds")
    @Mapping(source = "createTaskDto.dueDate", target = "dueDate", qualifiedByName = "unwrapOptional")
    @Mapping(source = "createTaskDto.priority", target = "priority", qualifiedByName = "unwrapOptional")
    CreateTaskCommand mapToCommand(CreateTaskDto createTaskDto, UUID boardId);

    @Mapping(source = "updateTaskDto.name", target = "name", qualifiedByName = "unwrapOptional")
    @Mapping(source = "updateTaskDto.description", target = "description", qualifiedByName = "unwrapOptional")
    @Mapping(source = "updateTaskDto.statusId", target = "statusId", qualifiedByName = "unwrapOptional")
    @Mapping(source = "updateTaskDto.assigneeId", target = "assigneeId", qualifiedByName = "unwrapOptional")
    @Mapping(source = "updateTaskDto.componentIds", target = "componentIds", qualifiedByName = "unwrapOptionalComponentIds")
    @Mapping(source = "updateTaskDto.dueDate", target = "dueDate", qualifiedByName = "unwrapOptional")
    @Mapping(source = "updateTaskDto.priority", target = "priority", qualifiedByName = "unwrapOptional")
    UpdateTaskByIdCommand mapToCommand(UUID id, UpdateTaskDto updateTaskDto);

    @Mapping(source = "content", target = "content", defaultExpression = "java(new ArrayList<>())")
    PageDto<TaskDto> mapToDto(Page<TaskProjection> taskProjectionPage);

    @Named("unwrapOptional")
    default <T> T unwrapOptional(Optional<T> optional) {
        return optional.orElse(null);
    }

    @Named("unwrapOptionalComponentIds")
    default Set<UUID> unwrapOptionalComponentIds(Optional<Set<String>> optional) {
        Set<String> unwrappedSet = optional.orElse(null);

        if(unwrappedSet != null) {
            return unwrappedSet.stream().map(UUID::fromString).collect(Collectors.toSet());
        } else {
            return null;
        }
    }

    default Priority mapPriority(String priority) {
        if(priority != null) {
            return Priority.valueOf(priority);
        } else {
            return null;
        }
    }

    default UUID mapUUID(String uuid) {
        if(uuid != null) {
            return UUID.fromString(uuid);
        } else {
            return null;
        }
    }

    default String mapOffsetDateTime(OffsetDateTime offsetDateTime) {
        if(offsetDateTime != null) {
            return offsetDateTime.toString();
        } else {
            return null;
        }
    }
}
