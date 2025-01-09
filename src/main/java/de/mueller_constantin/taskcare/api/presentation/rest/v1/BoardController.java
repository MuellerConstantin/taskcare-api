package de.mueller_constantin.taskcare.api.presentation.rest.v1;

import de.mueller_constantin.taskcare.api.core.kanban.application.*;
import de.mueller_constantin.taskcare.api.infrastructure.security.CurrentPrincipal;
import de.mueller_constantin.taskcare.api.infrastructure.security.Principal;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.BoardDto;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.CreateBoardDto;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.PageDto;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.UpdateBoardDto;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.mapper.BoardDtoMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class BoardController {
    private final BoardService boardService;
    private final BoardDtoMapper boardDtoMapper;

    @Autowired
    public BoardController(BoardService boardService, BoardDtoMapper boardDtoMapper) {
        this.boardService = boardService;
        this.boardDtoMapper = boardDtoMapper;
    }

    @GetMapping("/user/me/boards")
    public PageDto<BoardDto> getCurrentUsersBoards(@CurrentPrincipal Principal principal,
                                                   @RequestParam(required = false, defaultValue = "0") @Min(0) int page,
                                                   @RequestParam(required = false, defaultValue = "25") @Min(0) int perPage) {
        return boardDtoMapper.mapToDto(boardService.query(FindAllBoardsUserIsMemberQuery.builder()
                .userId(principal.getUserProjection().getId())
                .page(page)
                .perPage(perPage)
                .build()
        ));
    }

    @GetMapping("/boards/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR') or @domainSecurityService.isBoardMember(#id, principal.getUserProjection().getId())")
    public BoardDto getBoard(@PathVariable UUID id) {
        return boardDtoMapper.mapToDto(boardService.query(FindBoardByIdQuery.builder()
                .id(id)
                .build()));
    }

    @GetMapping("/boards")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public PageDto<BoardDto> getBoards(@RequestParam(required = false, defaultValue = "0") @Min(0) int page,
                                       @RequestParam(required = false, defaultValue = "25") @Min(0) int perPage) {
        return boardDtoMapper.mapToDto(boardService.query(FindAllBoardsQuery.builder()
                .page(page)
                .perPage(perPage)
                .build()
        ));
    }

    @PostMapping("/boards")
    @ResponseStatus(HttpStatus.CREATED)
    void createBoard(@CurrentPrincipal Principal principal,
                     @RequestBody @Valid CreateBoardDto createBoardDto) {
        boardService.dispatch(boardDtoMapper.mapToCommand(createBoardDto,
                principal.getUserProjection().getId()));
    }

    @DeleteMapping("/boards/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMINISTRATOR') or @domainSecurityService.isBoardMemberWithRole(#id, principal.getUserProjection().getId(), 'ADMINISTRATOR')")
    void deleteBoard(@PathVariable UUID id) {
        boardService.dispatch(DeleteBoardByIdCommand.builder()
                .id(id)
                .build());
    }

    @PatchMapping("/boards/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMINISTRATOR') or @domainSecurityService.isBoardMemberWithRole(#id, principal.getUserProjection().getId(), 'ADMINISTRATOR')")
    void updateBoard(@PathVariable UUID id, @RequestBody @Valid UpdateBoardDto updateBoardDto) {
        boardService.dispatch(boardDtoMapper.mapToCommand(id, updateBoardDto));
    }
}
