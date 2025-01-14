package de.mueller_constantin.taskcare.api.presentation.rest.v1;

import de.mueller_constantin.taskcare.api.core.kanban.application.KanbanReadService;
import de.mueller_constantin.taskcare.api.core.kanban.application.KanbanWriteService;
import de.mueller_constantin.taskcare.api.core.kanban.application.command.RemoveStatusByIdCommand;
import de.mueller_constantin.taskcare.api.core.kanban.application.query.FindAllStatusesByBoardIdQuery;
import de.mueller_constantin.taskcare.api.core.kanban.application.query.FindStatusByIdAndBoardIdQuery;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.AddStatusDto;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.PageDto;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.StatusDto;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.UpdateStatusDto;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.mapper.StatusDtoMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class StatusController {
    private final KanbanWriteService kanbanWriteService;
    private final KanbanReadService kanbanReadService;
    private final StatusDtoMapper statusDtoMapper;

    @Autowired
    public StatusController(KanbanWriteService kanbanWriteService, KanbanReadService kanbanReadService, StatusDtoMapper statusDtoMapper) {
        this.kanbanWriteService = kanbanWriteService;
        this.kanbanReadService = kanbanReadService;
        this.statusDtoMapper = statusDtoMapper;
    }

    @GetMapping("/boards/{id}/statuses")
    @PreAuthorize("hasRole('ADMINISTRATOR') or @domainSecurityService.isBoardMember(#id, principal.getUserProjection().getId())")
    public PageDto<StatusDto> getStatuses(@PathVariable UUID id,
                                          @RequestParam(required = false, defaultValue = "0") @Min(0) int page,
                                          @RequestParam(required = false, defaultValue = "25") @Min(0) int perPage) {
        return statusDtoMapper.mapToDto(kanbanReadService.query(FindAllStatusesByBoardIdQuery.builder()
                .boardId(id)
                .page(page)
                .perPage(perPage)
                .build()));
    }

    @GetMapping("/boards/{id}/statuses/{statusId}")
    @PreAuthorize("hasRole('ADMINISTRATOR') or @domainSecurityService.isBoardMember(#id, principal.getUserProjection().getId())")
    public StatusDto getStatus(@PathVariable UUID id, @PathVariable UUID statusId) {
        return statusDtoMapper.mapToDto(kanbanReadService.query(FindStatusByIdAndBoardIdQuery.builder()
                .id(statusId)
                .boardId(id)
                .build()));
    }

    @PostMapping("/boards/{id}/statuses")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMINISTRATOR') or @domainSecurityService.isBoardMemberWithAnyRole(#id, principal.getUserProjection().getId(), 'ADMINISTRATOR', 'MAINTAINER')")
    public void addStatus(@PathVariable UUID id, @RequestBody @Valid AddStatusDto addStatusDto) {
        kanbanWriteService.dispatch(statusDtoMapper.mapToCommand(id, addStatusDto));
    }

    @DeleteMapping("/boards/{id}/statuses/{statusId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMINISTRATOR') or @domainSecurityService.isBoardMemberWithAnyRole(#id, principal.getUserProjection().getId(), 'ADMINISTRATOR', 'MAINTAINER')")
    public void removeStatus(@PathVariable UUID id, @PathVariable UUID statusId) {
        kanbanWriteService.dispatch(RemoveStatusByIdCommand.builder()
                .boardId(id)
                .statusId(statusId)
                .build());
    }

    @PatchMapping("/boards/{id}/statuses/{statusId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMINISTRATOR') or @domainSecurityService.isBoardMemberWithAnyRole(#id, principal.getUserProjection().getId(), 'ADMINISTRATOR', 'MAINTAINER')")
    public void updateStatus(@PathVariable UUID id, @PathVariable UUID statusId, @RequestBody @Valid UpdateStatusDto updateStatusDto) {
        kanbanWriteService.dispatch(statusDtoMapper.mapToCommand(id, statusId, updateStatusDto));
    }
}
