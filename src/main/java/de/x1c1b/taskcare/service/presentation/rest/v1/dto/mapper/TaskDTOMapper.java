package de.x1c1b.taskcare.service.presentation.rest.v1.dto.mapper;

import de.x1c1b.taskcare.service.core.board.application.command.AddTaskByIdCommand;
import de.x1c1b.taskcare.service.core.board.application.command.UpdateTaskByIdCommand;
import de.x1c1b.taskcare.service.core.board.domain.Task;
import de.x1c1b.taskcare.service.presentation.rest.v1.dto.CreateTaskDTO;
import de.x1c1b.taskcare.service.presentation.rest.v1.dto.TaskDTO;
import de.x1c1b.taskcare.service.presentation.rest.v1.dto.UpdateTaskDTO;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface TaskDTOMapper {

    TaskDTO mapToDTO(Task taskAggregate);

    List<TaskDTO> mapToDTO(List<Task> taskAggregateList);

    AddTaskByIdCommand mapToCommand(CreateTaskDTO createTaskDTO, UUID id, String creator);

    UpdateTaskByIdCommand mapToCommand(UpdateTaskDTO updateTaskDTO, UUID id, UUID taskId);
}
