package de.x1c1b.taskcare.service.presentation.rest.v1.dto.mapper;

import de.x1c1b.taskcare.service.core.common.domain.Page;
import de.x1c1b.taskcare.service.core.user.application.command.CreateUserCommand;
import de.x1c1b.taskcare.service.core.user.application.command.UpdateUserByUsernameCommand;
import de.x1c1b.taskcare.service.core.user.domain.User;
import de.x1c1b.taskcare.service.presentation.rest.v1.dto.CreateUserDTO;
import de.x1c1b.taskcare.service.presentation.rest.v1.dto.PageDTO;
import de.x1c1b.taskcare.service.presentation.rest.v1.dto.UpdateUserDTO;
import de.x1c1b.taskcare.service.presentation.rest.v1.dto.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserDTOMapper {

    UserDTO mapToDTO(User userAggregate);

    @Mapping(source = "content", target = "content", defaultExpression = "java(new ArrayList<>())")
    PageDTO<UserDTO> mapToDTO(Page<User> userAggregatePage);

    CreateUserCommand mapToCommand(CreateUserDTO createUserDTO);

    UpdateUserByUsernameCommand mapToCommand(UpdateUserDTO updateUserDTO, String username);
}
