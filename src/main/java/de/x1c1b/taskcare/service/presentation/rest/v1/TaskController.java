package de.x1c1b.taskcare.service.presentation.rest.v1;

import de.x1c1b.taskcare.service.core.board.application.BoardService;
import de.x1c1b.taskcare.service.core.board.application.command.AddTaskByIdCommand;
import de.x1c1b.taskcare.service.core.board.application.command.RemoveTaskByIdCommand;
import de.x1c1b.taskcare.service.core.board.application.command.UpdateTaskByIdCommand;
import de.x1c1b.taskcare.service.core.board.application.query.FindBoardByIdQuery;
import de.x1c1b.taskcare.service.core.board.domain.Board;
import de.x1c1b.taskcare.service.core.common.application.EntityNotFoundException;
import de.x1c1b.taskcare.service.presentation.rest.v1.dto.CreateTaskDTO;
import de.x1c1b.taskcare.service.presentation.rest.v1.dto.TaskDTO;
import de.x1c1b.taskcare.service.presentation.rest.v1.dto.UpdateTaskDTO;
import de.x1c1b.taskcare.service.presentation.rest.v1.dto.mapper.TaskDTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1")
@Validated
public class TaskController {

    private final BoardService boardService;
    private final TaskDTOMapper taskDTOMapper;

    @Autowired
    public TaskController(BoardService boardService, TaskDTOMapper taskDTOMapper) {
        this.boardService = boardService;
        this.taskDTOMapper = taskDTOMapper;
    }

    @GetMapping("/boards/{id}/tasks")
    List<TaskDTO> findAll(@PathVariable("id") UUID id) {
        FindBoardByIdQuery query = new FindBoardByIdQuery(id);
        Board result = boardService.query(query);
        return taskDTOMapper.mapToDTO(result.getTasks());
    }

    @GetMapping("/boards/{id}/tasks/{taskId}")
    TaskDTO findByUsername(@PathVariable("id") UUID id, @PathVariable("taskId") UUID taskId) {
        FindBoardByIdQuery query = new FindBoardByIdQuery(id);
        Board result = boardService.query(query);
        return taskDTOMapper.mapToDTO(result.getTasks().stream()
                .filter(task -> task.getId().equals(taskId)).findFirst().orElseThrow(EntityNotFoundException::new));
    }

    @PatchMapping("/boards/{id}/tasks/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void updateByUsername(@PathVariable("id") UUID id, @PathVariable("taskId") UUID taskId, @RequestBody UpdateTaskDTO updateTaskDTO) {
        UpdateTaskByIdCommand command = taskDTOMapper.mapToCommand(updateTaskDTO, id, taskId);
        boardService.execute(command);
    }

    @PostMapping("/boards/{id}/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    void addMember(@PathVariable("id") UUID id, @RequestBody CreateTaskDTO createTaskDTO,
                   @AuthenticationPrincipal UserDetails userDetails) {
        AddTaskByIdCommand command = taskDTOMapper.mapToCommand(createTaskDTO, id, userDetails.getUsername());
        boardService.execute(command);
    }

    @DeleteMapping("/boards/{id}/tasks/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void removeMember(@PathVariable("id") UUID id, @PathVariable("taskId") UUID taskId) {
        RemoveTaskByIdCommand command = new RemoveTaskByIdCommand(id, taskId);
        boardService.execute(command);
    }
}
