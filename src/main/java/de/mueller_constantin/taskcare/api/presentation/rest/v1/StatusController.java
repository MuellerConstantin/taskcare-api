package de.mueller_constantin.taskcare.api.presentation.rest.v1;

import de.mueller_constantin.taskcare.api.core.board.application.BoardReadService;
import de.mueller_constantin.taskcare.api.core.board.application.BoardWriteService;
import de.mueller_constantin.taskcare.api.core.board.application.command.RemoveStatusByIdCommand;
import de.mueller_constantin.taskcare.api.core.board.application.query.FindAllStatusesByBoardIdQuery;
import de.mueller_constantin.taskcare.api.core.board.application.query.FindStatusByIdAndBoardIdQuery;
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
    private final BoardWriteService boardWriteService;
    private final BoardReadService boardReadService;
    private final StatusDtoMapper statusDtoMapper;

    @Autowired
    public StatusController(BoardWriteService boardWriteService, BoardReadService boardReadService, StatusDtoMapper statusDtoMapper) {
        this.boardWriteService = boardWriteService;
        this.boardReadService = boardReadService;
        this.statusDtoMapper = statusDtoMapper;
    }

    @GetMapping("/boards/{id}/statuses")
    @PreAuthorize("hasRole('ADMINISTRATOR') or @domainSecurityService.isBoardMember(#id, principal.getUserProjection().getId())")
    public PageDto<StatusDto> getStatuses(@PathVariable UUID id,
                                          @RequestParam(required = false, defaultValue = "0") @Min(0) int page,
                                          @RequestParam(required = false, defaultValue = "25") @Min(0) int perPage) {
        return statusDtoMapper.mapToDto(boardReadService.query(FindAllStatusesByBoardIdQuery.builder()
                .boardId(id)
                .page(page)
                .perPage(perPage)
                .build()));
    }

    @GetMapping("/boards/{id}/statuses/{statusId}")
    @PreAuthorize("hasRole('ADMINISTRATOR') or @domainSecurityService.isBoardMember(#id, principal.getUserProjection().getId())")
    public StatusDto getStatus(@PathVariable UUID id, @PathVariable UUID statusId) {
        return statusDtoMapper.mapToDto(boardReadService.query(FindStatusByIdAndBoardIdQuery.builder()
                .id(statusId)
                .boardId(id)
                .build()));
    }

    @PostMapping("/boards/{id}/statuses")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMINISTRATOR') or @domainSecurityService.isBoardMemberWithAnyRole(#id, principal.getUserProjection().getId(), 'ADMINISTRATOR', 'MAINTAINER')")
    public void addStatus(@PathVariable UUID id, @RequestBody @Valid AddStatusDto addStatusDto) {
        boardWriteService.dispatch(statusDtoMapper.mapToCommand(id, addStatusDto));
    }

    @DeleteMapping("/boards/{id}/statuses/{statusId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMINISTRATOR') or @domainSecurityService.isBoardMemberWithAnyRole(#id, principal.getUserProjection().getId(), 'ADMINISTRATOR', 'MAINTAINER')")
    public void removeStatus(@PathVariable UUID id, @PathVariable UUID statusId) {
        boardWriteService.dispatch(RemoveStatusByIdCommand.builder()
                .boardId(id)
                .statusId(statusId)
                .build());
    }

    @PatchMapping("/boards/{id}/statuses/{statusId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMINISTRATOR') or @domainSecurityService.isBoardMemberWithAnyRole(#id, principal.getUserProjection().getId(), 'ADMINISTRATOR', 'MAINTAINER')")
    public void updateStatus(@PathVariable UUID id, @PathVariable UUID statusId, @RequestBody @Valid UpdateStatusDto updateStatusDto) {
        boardWriteService.dispatch(statusDtoMapper.mapToCommand(id, statusId, updateStatusDto));
    }
}
