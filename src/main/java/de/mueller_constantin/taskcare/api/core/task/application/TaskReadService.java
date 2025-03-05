package de.mueller_constantin.taskcare.api.core.task.application;

import de.mueller_constantin.taskcare.api.core.common.application.ApplicationService;
import de.mueller_constantin.taskcare.api.core.common.application.NoSuchEntityException;
import de.mueller_constantin.taskcare.api.core.common.domain.Page;
import de.mueller_constantin.taskcare.api.core.common.domain.PageInfo;
import de.mueller_constantin.taskcare.api.core.task.application.persistence.TaskReadModelRepository;
import de.mueller_constantin.taskcare.api.core.task.application.query.*;
import de.mueller_constantin.taskcare.api.core.task.domain.TaskProjection;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TaskReadService implements ApplicationService {
    private final TaskReadModelRepository taskReadModelRepository;

    public TaskReadService(TaskReadModelRepository taskReadModelRepository) {
        this.taskReadModelRepository = taskReadModelRepository;
    }

    public TaskProjection query(FindTaskByIdQuery query) {
        return taskReadModelRepository.findById(query.getId())
                .orElseThrow(NoSuchEntityException::new);
    }

    public Page<TaskProjection> query(FindAllTasksByBoardIdQuery query) {
        return taskReadModelRepository.findAllByBoardId(query.getBoardId(), PageInfo.builder()
                .page(query.getPage())
                .perPage(query.getPerPage())
                .build());
    }

    public Page<TaskProjection> query(FindAllTasksByBoardIdAndAssigneeIdQuery query) {
        return taskReadModelRepository.findAllByBoardIdAndAssigneeId(query.getBoardId(), query.getAssigneeId(), PageInfo.builder()
                .page(query.getPage())
                .perPage(query.getPerPage())
                .build());
    }

    public Page<TaskProjection> query(FindAllTasksByBoardIdAndStatusIdQuery query) {
        return taskReadModelRepository.findAllByBoardIdAndStatusId(query.getBoardId(), query.getStatusId(), PageInfo.builder()
                .page(query.getPage())
                .perPage(query.getPerPage())
                .build());
    }

    public Page<TaskProjection> query(FindAllTasksByBoardIdAndComponentIdQuery query) {
        return taskReadModelRepository.findAllByBoardIdAndComponentId(query.getBoardId(), query.getComponentId(), PageInfo.builder()
                .page(query.getPage())
                .perPage(query.getPerPage())
                .build());
    }
}
