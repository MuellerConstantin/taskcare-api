package de.x1c1b.taskcare.service.presentation.rest.v1.dto.mapper;

import de.x1c1b.taskcare.service.core.board.application.command.AddMemberByIdCommand;
import de.x1c1b.taskcare.service.core.board.application.command.UpdateMemberByIdCommand;
import de.x1c1b.taskcare.service.core.board.domain.Member;
import de.x1c1b.taskcare.service.presentation.rest.v1.dto.CreateBoardMemberDTO;
import de.x1c1b.taskcare.service.presentation.rest.v1.dto.MemberDTO;
import de.x1c1b.taskcare.service.presentation.rest.v1.dto.UpdateBoardMemberDTO;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface MemberDTOMapper {

    MemberDTO mapToDTO(Member memberAggregate);

    List<MemberDTO> mapToDTO(Set<Member> memberAggregateSet);

    AddMemberByIdCommand mapToCommand(CreateBoardMemberDTO createBoardMemberDTO, UUID id);

    UpdateMemberByIdCommand mapToCommand(UpdateBoardMemberDTO updateBoardMemberDTO, UUID id, String username);
}