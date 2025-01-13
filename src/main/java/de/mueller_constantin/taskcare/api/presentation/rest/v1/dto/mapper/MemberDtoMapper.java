package de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.mapper;

import de.mueller_constantin.taskcare.api.core.kanban.application.command.AddMemberByIdCommand;
import de.mueller_constantin.taskcare.api.core.kanban.application.command.UpdateMemberByIdCommand;
import de.mueller_constantin.taskcare.api.core.kanban.domain.MemberProjection;
import de.mueller_constantin.taskcare.api.core.kanban.domain.Role;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.AddMemberDto;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.MemberDto;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.UpdateMemberDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface MemberDtoMapper {
    MemberDto mapToDto(MemberProjection memberProjection);

    AddMemberByIdCommand mapToCommand(UUID boardId, AddMemberDto addMemberDto);

    @Mapping(source = "updateMemberDto.role", target = "role", qualifiedByName = "unwrapOptional")
    UpdateMemberByIdCommand mapToCommand(UUID boardId, UUID memberId, UpdateMemberDto updateMemberDto);

    List<MemberDto> mapToDto(List<MemberProjection> memberProjectionPage);

    @Named("unwrapOptional")
    default <T> T unwrapOptional(Optional<T> optional) {
        return optional.orElse(null);
    }

    default Role mapRole(String role) {
        if(role != null) {
            return Role.valueOf(role);
        } else {
            return null;
        }
    }
}
