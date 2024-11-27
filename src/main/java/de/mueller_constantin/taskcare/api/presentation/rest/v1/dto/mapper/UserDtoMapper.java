package de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.mapper;

import de.mueller_constantin.taskcare.api.core.user.domain.model.UserProjection;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.UserDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserDtoMapper {
    UserDto mapToDTO(UserProjection userProjection);
}
