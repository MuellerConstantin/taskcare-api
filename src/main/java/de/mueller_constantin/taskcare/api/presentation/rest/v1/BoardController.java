package de.mueller_constantin.taskcare.api.presentation.rest.v1;

import de.mueller_constantin.taskcare.api.core.common.application.NoSuchEntityException;
import de.mueller_constantin.taskcare.api.core.common.application.persistence.MediaStorage;
import de.mueller_constantin.taskcare.api.core.kanban.application.*;
import de.mueller_constantin.taskcare.api.core.kanban.domain.BoardProjection;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class BoardController {
    private final BoardService boardService;
    private final BoardDtoMapper boardDtoMapper;
    private final MediaStorage mediaStorage;

    @Autowired
    public BoardController(BoardService boardService, BoardDtoMapper boardDtoMapper, MediaStorage mediaStorage) {
        this.boardService = boardService;
        this.boardDtoMapper = boardDtoMapper;
        this.mediaStorage = mediaStorage;
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

    @PostMapping("/boards/{id}/logo-image")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMINISTRATOR') or @domainSecurityService.isBoardMemberWithRole(#id, principal.getUserProjection().getId(), 'ADMINISTRATOR')")
    void uploadLogoImage(@PathVariable UUID id, @RequestParam("file") MultipartFile file) throws IOException {
        FindBoardByIdQuery query = new FindBoardByIdQuery(id);
        BoardProjection result = boardService.query(query);

        mediaStorage.save("/logo-images/" + result.getId().toString(), file.getContentType(), file.getBytes());
    }

    @DeleteMapping("/boards/{id}/logo-image")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMINISTRATOR') or @domainSecurityService.isBoardMemberWithRole(#id, principal.getUserProjection().getId(), 'ADMINISTRATOR')")
    void removeProfileImage(@PathVariable UUID id) {
        FindBoardByIdQuery query = new FindBoardByIdQuery(id);
        BoardProjection result = boardService.query(query);

        mediaStorage.delete("/logo-images/" + result.getId().toString());
    }

    @GetMapping("/boards/{id}/logo-image")
    public ResponseEntity<byte[]> getLogoImage(@PathVariable UUID id) {
        FindBoardByIdQuery query = new FindBoardByIdQuery(id);
        BoardProjection result = boardService.query(query);

        if(mediaStorage.exists("/logo-images/" + result.getId().toString())) {
            String contentType = mediaStorage.contentType("/logo-images/" + result.getId().toString());
            byte[] data = mediaStorage.load("/logo-images/" + result.getId().toString());

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .contentLength(data.length)
                    .body(data);
        } else {
            throw new NoSuchEntityException();
        }
    }
}
