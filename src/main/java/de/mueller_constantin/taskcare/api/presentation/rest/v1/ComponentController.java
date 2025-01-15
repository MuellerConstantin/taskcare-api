package de.mueller_constantin.taskcare.api.presentation.rest.v1;

import de.mueller_constantin.taskcare.api.core.board.application.BoardReadService;
import de.mueller_constantin.taskcare.api.core.board.application.BoardWriteService;
import de.mueller_constantin.taskcare.api.core.board.application.command.RemoveComponentByIdCommand;
import de.mueller_constantin.taskcare.api.core.board.application.query.FindAllComponentsByBoardIdQuery;
import de.mueller_constantin.taskcare.api.core.board.application.query.FindComponentByIdAndBoardIdQuery;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.AddComponentDto;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.ComponentDto;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.PageDto;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.UpdateComponentDto;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.mapper.ComponentDtoMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class ComponentController {
    private final BoardWriteService boardWriteService;
    private final BoardReadService boardReadService;
    private final ComponentDtoMapper componentDtoMapper;

    @Autowired
    public ComponentController(BoardWriteService boardWriteService, BoardReadService boardReadService, ComponentDtoMapper componentDtoMapper) {
        this.boardWriteService = boardWriteService;
        this.boardReadService = boardReadService;
        this.componentDtoMapper = componentDtoMapper;
    }

    @GetMapping("/boards/{id}/components")
    @PreAuthorize("hasRole('ADMINISTRATOR') or @domainSecurityService.isBoardMember(#id, principal.getUserProjection().getId())")
    public PageDto<ComponentDto> getComponents(@PathVariable UUID id,
                                               @RequestParam(required = false, defaultValue = "0") @Min(0) int page,
                                               @RequestParam(required = false, defaultValue = "25") @Min(0) int perPage) {
        return componentDtoMapper.mapToDto(boardReadService.query(FindAllComponentsByBoardIdQuery.builder()
                .boardId(id)
                .page(page)
                .perPage(perPage)
                .build()));
    }

    @GetMapping("/boards/{id}/components/{componentId}")
    @PreAuthorize("hasRole('ADMINISTRATOR') or @domainSecurityService.isBoardMember(#id, principal.getUserProjection().getId())")
    public ComponentDto getComponent(@PathVariable UUID id, @PathVariable UUID componentId) {
        return componentDtoMapper.mapToDto(boardReadService.query(FindComponentByIdAndBoardIdQuery.builder()
                .id(componentId)
                .boardId(id)
                .build()));
    }

    @PostMapping("/boards/{id}/components")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMINISTRATOR') or @domainSecurityService.isBoardMemberWithAnyRole(#id, principal.getUserProjection().getId(), 'ADMINISTRATOR', 'MAINTAINER')")
    public void addComponent(@PathVariable UUID id, @RequestBody @Valid AddComponentDto addComponentDto) {
        boardWriteService.dispatch(componentDtoMapper.mapToCommand(id, addComponentDto));
    }

    @DeleteMapping("/boards/{id}/components/{componentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMINISTRATOR') or @domainSecurityService.isBoardMemberWithAnyRole(#id, principal.getUserProjection().getId(), 'ADMINISTRATOR', 'MAINTAINER')")
    public void removeComponent(@PathVariable UUID id, @PathVariable UUID componentId) {
        boardWriteService.dispatch(RemoveComponentByIdCommand.builder()
                .boardId(id)
                .componentId(componentId)
                .build());
    }

    @PatchMapping("/boards/{id}/components/{componentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMINISTRATOR') or @domainSecurityService.isBoardMemberWithAnyRole(#id, principal.getUserProjection().getId(), 'ADMINISTRATOR', 'MAINTAINER')")
    public void updateComponent(@PathVariable UUID id, @PathVariable UUID componentId, @RequestBody @Valid UpdateComponentDto updateComponentDto) {
        boardWriteService.dispatch(componentDtoMapper.mapToCommand(id, componentId, updateComponentDto));
    }
}
