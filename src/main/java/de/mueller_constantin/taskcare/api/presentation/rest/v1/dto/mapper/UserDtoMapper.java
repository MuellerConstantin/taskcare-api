package de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.mapper;

import de.mueller_constantin.taskcare.api.core.common.domain.model.Page;
import de.mueller_constantin.taskcare.api.core.user.application.service.CreateUserCommand;
import de.mueller_constantin.taskcare.api.core.user.application.service.UpdateUserByIdCommand;
import de.mueller_constantin.taskcare.api.core.user.domain.model.Role;
import de.mueller_constantin.taskcare.api.core.user.domain.model.UserProjection;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.CreateUserDto;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.PageDto;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.UpdateUserDto;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Optional;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface UserDtoMapper {
    UserDto mapToDto(UserProjection userProjection);

    @Mapping(source = "displayName", target = "displayName", qualifiedByName = "unwrapOptional")
    @Mapping(target = "identityProvider", constant = "LOCAL")
    CreateUserCommand mapToCommand(CreateUserDto createUserDto);

    @Mapping(source = "updateUserDto.password", target = "password", qualifiedByName = "unwrapOptional")
    @Mapping(source = "updateUserDto.role", target = "role", qualifiedByName = "unwrapOptional")
    @Mapping(source = "updateUserDto.displayName", target = "displayName", qualifiedByName = "unwrapOptional")
    UpdateUserByIdCommand mapToCommand(UUID id, UpdateUserDto updateUserDto);

    @Mapping(source = "content", target = "content", defaultExpression = "java(new ArrayList<>())")
    PageDto<UserDto> mapToDTO(Page<UserProjection> userProjectionPage);

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
