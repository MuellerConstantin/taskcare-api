package de.x1c1b.taskcare.api.presentation.rest.v1.dto.mapper;

import de.x1c1b.taskcare.api.core.board.application.command.CreateMemberByIdCommand;
import de.x1c1b.taskcare.api.core.board.application.command.UpdateMemberByIdCommand;
import de.x1c1b.taskcare.api.core.board.domain.Member;
import de.x1c1b.taskcare.api.presentation.rest.v1.dto.CreateMemberDTO;
import de.x1c1b.taskcare.api.presentation.rest.v1.dto.MemberDTO;
import de.x1c1b.taskcare.api.presentation.rest.v1.dto.UpdateMemberDTO;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface MemberDTOMapper {

    MemberDTO mapToDTO(Member memberAggregate);

    List<MemberDTO> mapToDTO(Set<Member> memberAggregateSet);

    CreateMemberByIdCommand mapToCommand(CreateMemberDTO createMemberDTO, UUID id);

    UpdateMemberByIdCommand mapToCommand(UpdateMemberDTO updateMemberDTO, UUID id, String username);
}
