package de.mueller_constantin.taskcare.api.presentation.rest.v1;

import de.mueller_constantin.taskcare.api.core.board.application.BoardReadService;
import de.mueller_constantin.taskcare.api.core.board.application.BoardWriteService;
import de.mueller_constantin.taskcare.api.core.board.application.query.FindAllMembersByBoardIdQuery;
import de.mueller_constantin.taskcare.api.core.board.application.command.RemoveMemberByIdCommand;
import de.mueller_constantin.taskcare.api.core.board.application.query.FindMemberByIdAndBoardIdQuery;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.AddMemberDto;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.MemberDto;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.PageDto;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.UpdateMemberDto;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.mapper.MemberDtoMapper;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.search.SearchFilter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class MemberController {
    private final BoardWriteService boardWriteService;
    private final BoardReadService boardReadService;
    private final MemberDtoMapper memberDtoMapper;

    @Autowired
    public MemberController(BoardWriteService boardWriteService,
                            BoardReadService boardReadService,
                            MemberDtoMapper memberDtoMapper) {
        this.boardWriteService = boardWriteService;
        this.boardReadService = boardReadService;
        this.memberDtoMapper = memberDtoMapper;
    }

    @GetMapping("/boards/{id}/members")
    @PreAuthorize("hasRole('ADMINISTRATOR') or @domainSecurityService.isBoardMember(#id, principal.getUserProjection().getId())")
    public PageDto<MemberDto> getMembers(@PathVariable UUID id,
                                         @RequestParam(required = false, defaultValue = "0") @Min(0) int page,
                                         @RequestParam(required = false, defaultValue = "25") @Min(0) int perPage,
                                         @RequestParam(required = false) @SearchFilter String search) {
        return memberDtoMapper.mapToDto(boardReadService.query(FindAllMembersByBoardIdQuery.builder()
                .boardId(id)
                .page(page)
                .perPage(perPage)
                .search(search)
                .build()));
    }

    @GetMapping("/boards/{boardId}/members/{memberId}")
    @PreAuthorize("hasRole('ADMINISTRATOR') or @domainSecurityService.isBoardMember(#boardId, principal.getUserProjection().getId())")
    public MemberDto getMember(@PathVariable UUID boardId, @PathVariable UUID memberId) {
        return memberDtoMapper.mapToDto(boardReadService.query(FindMemberByIdAndBoardIdQuery.builder()
                .id(memberId)
                .boardId(boardId)
                .build()));
    }

    @PostMapping("/boards/{id}/members")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMINISTRATOR') or @domainSecurityService.isBoardMemberWithRole(#id, principal.getUserProjection().getId(), 'ADMINISTRATOR')")
    public void addMember(@PathVariable UUID id, @RequestBody @Valid AddMemberDto addMemberDto) {
        boardWriteService.dispatch(memberDtoMapper.mapToCommand(id, addMemberDto));
    }

    @DeleteMapping("/boards/{id}/members/{memberId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMINISTRATOR') or @domainSecurityService.isBoardMemberWithRole(#id, principal.getUserProjection().getId(), 'ADMINISTRATOR')")
    public void removeMember(@PathVariable UUID id, @PathVariable UUID memberId) {
        boardWriteService.dispatch(RemoveMemberByIdCommand.builder()
                .boardId(id)
                .memberId(memberId)
                .build());
    }

    @PatchMapping("/boards/{id}/members/{memberId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMINISTRATOR') or @domainSecurityService.isBoardMemberWithRole(#id, principal.getUserProjection().getId(), 'ADMINISTRATOR')")
    public void updateMemberRole(@PathVariable UUID id, @PathVariable UUID memberId,
                                 @RequestBody @Valid UpdateMemberDto updateMemberDto) {
        boardWriteService.dispatch(memberDtoMapper.mapToCommand(id, memberId, updateMemberDto));
    }
}
