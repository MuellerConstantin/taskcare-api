package de.mueller_constantin.taskcare.api.presentation.rest.v1;

import de.mueller_constantin.taskcare.api.core.user.application.service.FindUserByUsernameQuery;
import de.mueller_constantin.taskcare.api.core.user.application.service.UserService;
import de.mueller_constantin.taskcare.api.core.user.domain.model.UserProjection;
import de.mueller_constantin.taskcare.api.infrastructure.security.CurrentPrincipal;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.CreateUserDto;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.UserDto;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.mapper.UserDtoMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    private final UserService userService;
    private final UserDtoMapper userDtoMapper;

    @Autowired
    public UserController(UserService userService, UserDtoMapper userDtoMapper) {
        this.userService = userService;
        this.userDtoMapper = userDtoMapper;
    }

    @GetMapping("/user/me")
    UserDto getCurrentUser(@CurrentPrincipal UserDetails userDetails) {
        FindUserByUsernameQuery query = new FindUserByUsernameQuery(userDetails.getUsername());
        UserProjection result = userService.handle(query);
        System.out.println(result);
        return userDtoMapper.mapToDto(result);
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    void createUser(@RequestBody @Valid CreateUserDto createUserDto) {
        userService.handle(userDtoMapper.mapToCommand(createUserDto));
    }
}
