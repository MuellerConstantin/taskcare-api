package de.mueller_constantin.taskcare.api.presentation.rest.v1;

import de.mueller_constantin.taskcare.api.core.task.application.TaskReadService;
import de.mueller_constantin.taskcare.api.core.task.application.TaskWriteService;
import de.mueller_constantin.taskcare.api.core.task.application.command.DeleteTaskByIdCommand;
import de.mueller_constantin.taskcare.api.core.task.application.query.*;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.CreateTaskDto;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.PageDto;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.TaskDto;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.UpdateTaskDto;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.mapper.TaskDtoMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class TaskController {
    private final TaskWriteService taskWriteService;
    private final TaskReadService taskReadService;
    private final TaskDtoMapper taskDtoMapper;

    @Autowired
    public TaskController(TaskWriteService taskWriteService, TaskReadService taskReadService, TaskDtoMapper taskDtoMapper) {
        this.taskWriteService = taskWriteService;
        this.taskReadService = taskReadService;
        this.taskDtoMapper = taskDtoMapper;
    }

    @GetMapping("/tasks/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR') or @domainSecurityService.isMemberOfTasksBoard(#id, principal.getUserProjection().getId())")
    public TaskDto getTask(@PathVariable UUID id) {
        return taskDtoMapper.mapToDto(taskReadService.query(new FindTaskByIdQuery(id)));
    }

    @GetMapping("/boards/{id}/tasks")
    @PreAuthorize("hasRole('ADMINISTRATOR') or @domainSecurityService.isBoardMember(#id, principal.getUserProjection().getId())")
    public PageDto<TaskDto> getTasksByBoard(@PathVariable UUID id,
                                              @RequestParam(required = false, defaultValue = "0") @Min(0) int page,
                                              @RequestParam(required = false, defaultValue = "25") @Min(0) int perPage) {
        return taskDtoMapper.mapToDto(taskReadService.query(FindAllTasksByBoardIdQuery.builder()
                .boardId(id)
                .page(page)
                .perPage(perPage)
                .build()));
    }

    @GetMapping("/boards/{boardId}/tasks/no-status")
    @PreAuthorize("hasRole('ADMINISTRATOR') or @domainSecurityService.isBoardMember(#boardId, principal.getUserProjection().getId())")
    public PageDto<TaskDto> getTasksByStatusNone(@PathVariable UUID boardId,
                                                 @RequestParam(required = false, defaultValue = "0") @Min(0) int page,
                                                 @RequestParam(required = false, defaultValue = "25") @Min(0) int perPage) {
        return taskDtoMapper.mapToDto(taskReadService.query(FindAllTasksByBoardIdAndNoStatusQuery.builder()
                .boardId(boardId)
                .page(page)
                .perPage(perPage)
                .build()));
    }

    @GetMapping("/boards/{boardId}/statuses/{statusId}/tasks")
    @PreAuthorize("hasRole('ADMINISTRATOR') or @domainSecurityService.isBoardMember(#boardId, principal.getUserProjection().getId())")
    public PageDto<TaskDto> getTasksByStatus(@PathVariable UUID boardId, @PathVariable UUID statusId,
                                     @RequestParam(required = false, defaultValue = "0") @Min(0) int page,
                                     @RequestParam(required = false, defaultValue = "25") @Min(0) int perPage) {
        return taskDtoMapper.mapToDto(taskReadService.query(FindAllTasksByBoardIdAndStatusIdQuery.builder()
                .boardId(boardId)
                .statusId(statusId)
                .page(page)
                .perPage(perPage)
                .build()));
    }

    @GetMapping("/boards/{boardId}/components/{componentId}/tasks")
    @PreAuthorize("hasRole('ADMINISTRATOR') or @domainSecurityService.isBoardMember(#boardId, principal.getUserProjection().getId())")
    public PageDto<TaskDto> getTasksByComponent(@PathVariable UUID boardId, @PathVariable UUID componentId,
                                     @RequestParam(required = false, defaultValue = "0") @Min(0) int page,
                                     @RequestParam(required = false, defaultValue = "25") @Min(0) int perPage) {
        return taskDtoMapper.mapToDto(taskReadService.query(FindAllTasksByBoardIdAndComponentIdQuery.builder()
                .boardId(boardId)
                .componentId(componentId)
                .page(page)
                .perPage(perPage)
                .build()));
    }

    @GetMapping("/boards/{boardId}/members/{memberId}/tasks")
    @PreAuthorize("hasRole('ADMINISTRATOR') or @domainSecurityService.isBoardMember(#boardId, principal.getUserProjection().getId())")
    public PageDto<TaskDto> getTasksByMember(@PathVariable UUID boardId, @PathVariable UUID memberId,
                                             @RequestParam(required = false, defaultValue = "0") @Min(0) int page,
                                             @RequestParam(required = false, defaultValue = "25") @Min(0) int perPage) {
        return taskDtoMapper.mapToDto(taskReadService.query(FindAllTasksByBoardIdAndAssigneeIdQuery.builder()
                .boardId(boardId)
                .assigneeId(memberId)
                .page(page)
                .perPage(perPage)
                .build()));
    }

    @PostMapping("/boards/{id}/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMINISTRATOR') or @domainSecurityService.isBoardMember(#id, principal.getUserProjection().getId())")
    void createTask(@PathVariable UUID id, @RequestBody @Valid CreateTaskDto createTaskDto) {
        taskWriteService.dispatch(taskDtoMapper.mapToCommand(createTaskDto, id));
    }

    @DeleteMapping("/tasks/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMINISTRATOR') or @domainSecurityService.isMemberOfTasksBoard(#id, principal.getUserProjection().getId())")
    void deleteTask(@PathVariable UUID id) {
        taskWriteService.dispatch(new DeleteTaskByIdCommand(id));
    }

    @PatchMapping("/tasks/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMINISTRATOR') or @domainSecurityService.isMemberOfTasksBoard(#id, principal.getUserProjection().getId())")
    void updateTask(@PathVariable UUID id, @RequestBody @Valid UpdateTaskDto updateTaskDto) {
        taskWriteService.dispatch(taskDtoMapper.mapToCommand(id, updateTaskDto));
    }
}
