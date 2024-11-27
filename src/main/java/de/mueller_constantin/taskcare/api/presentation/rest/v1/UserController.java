package de.mueller_constantin.taskcare.api.presentation.rest.v1;

import de.mueller_constantin.taskcare.api.core.user.application.service.FindUserByUsernameQuery;
import de.mueller_constantin.taskcare.api.core.user.application.service.UserService;
import de.mueller_constantin.taskcare.api.core.user.domain.model.UserProjection;
import de.mueller_constantin.taskcare.api.infrastructure.security.CurrentPrincipal;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.UserDto;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.mapper.UserDtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    private final UserService userService;
    private final UserDtoMapper userDTOMapper;

    @Autowired
    public UserController(UserService userService, UserDtoMapper userDTOMapper) {
        this.userService = userService;
        this.userDTOMapper = userDTOMapper;
    }

    @GetMapping("/user/me")
    UserDto getCurrentUser(@CurrentPrincipal UserDetails userDetails) {
        FindUserByUsernameQuery query = new FindUserByUsernameQuery(userDetails.getUsername());
        UserProjection result = userService.handle(query);
        System.out.println(result);
        return userDTOMapper.mapToDTO(result);
    }
}
