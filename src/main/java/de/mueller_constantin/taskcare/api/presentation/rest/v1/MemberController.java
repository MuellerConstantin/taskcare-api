package de.mueller_constantin.taskcare.api.presentation.rest.v1;

import de.mueller_constantin.taskcare.api.core.common.application.NoSuchEntityException;
import de.mueller_constantin.taskcare.api.core.kanban.application.BoardService;
import de.mueller_constantin.taskcare.api.core.kanban.application.FindBoardByIdQuery;
import de.mueller_constantin.taskcare.api.core.kanban.application.RemoveMemberByIdCommand;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.AddMemberDto;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.MemberDto;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.UpdateMemberDto;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.mapper.MemberDtoMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class MemberController {
    private final BoardService boardService;
    private final MemberDtoMapper memberDtoMapper;

    @Autowired
    public MemberController(BoardService boardService, MemberDtoMapper memberDtoMapper) {
        this.boardService = boardService;
        this.memberDtoMapper = memberDtoMapper;
    }

    @GetMapping("/boards/{id}/members")
    @PreAuthorize("hasRole('ADMINISTRATOR') or @domainSecurityService.isBoardMember(#id, principal.getUserProjection().getId())")
    public List<MemberDto> getMembers(@PathVariable UUID id) {
        return memberDtoMapper.mapToDto(new ArrayList<>(boardService.query(FindBoardByIdQuery.builder()
                .id(id)
                .build())
                .getMembers()));
    }

    @GetMapping("/boards/{id}/members/{memberId}")
    @PreAuthorize("hasRole('ADMINISTRATOR') or @domainSecurityService.isBoardMember(#id, principal.getUserProjection().getId())")
    public MemberDto getMember(@PathVariable UUID id, @PathVariable UUID memberId) {
        return memberDtoMapper.mapToDto(boardService.query(FindBoardByIdQuery.builder()
                .id(id)
                .build())
                .getMembers().stream()
                        .filter(m -> m.getId().equals(memberId))
                        .findFirst()
                        .orElseThrow(NoSuchEntityException::new)
                );
    }

    @PostMapping("/boards/{id}/members")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMINISTRATOR') or @domainSecurityService.isBoardMemberWithRole(#id, principal.getUserProjection().getId(), 'ADMINISTRATOR')")
    public void addMember(@PathVariable UUID id, @RequestBody @Valid AddMemberDto addMemberDto) {
        boardService.dispatch(memberDtoMapper.mapToCommand(id, addMemberDto));
    }

    @DeleteMapping("/boards/{id}/members/{memberId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMINISTRATOR') or @domainSecurityService.isBoardMemberWithRole(#id, principal.getUserProjection().getId(), 'ADMINISTRATOR')")
    public void removeMember(@PathVariable UUID id, @PathVariable UUID memberId) {
        boardService.dispatch(RemoveMemberByIdCommand.builder()
                .boardId(id)
                .memberId(memberId)
                .build());
    }

    @PatchMapping("/boards/{id}/members/{memberId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMINISTRATOR') or @domainSecurityService.isBoardMemberWithRole(#id, principal.getUserProjection().getId(), 'ADMINISTRATOR')")
    public void updateMemberRole(@PathVariable UUID id, @PathVariable UUID memberId,
                                 @RequestBody @Valid UpdateMemberDto updateMemberDto) {
        boardService.dispatch(memberDtoMapper.mapToCommand(id, memberId, updateMemberDto));
    }
}
