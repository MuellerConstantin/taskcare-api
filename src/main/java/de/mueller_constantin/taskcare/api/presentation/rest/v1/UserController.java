package de.mueller_constantin.taskcare.api.presentation.rest.v1;

import com.fasterxml.jackson.annotation.JsonView;
import de.mueller_constantin.taskcare.api.core.user.application.*;
import de.mueller_constantin.taskcare.api.core.user.domain.UserProjection;
import de.mueller_constantin.taskcare.api.infrastructure.security.CurrentPrincipal;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.CreateUserDto;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.PageDto;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.UpdateUserDto;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.UserDto;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.mapper.UserDtoMapper;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.view.DefaultJsonViews;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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
    @JsonView(DefaultJsonViews.Me.class)
    UserDto getCurrentUser(@CurrentPrincipal UserDetails userDetails) {
        FindUserByUsernameQuery query = new FindUserByUsernameQuery(userDetails.getUsername());
        UserProjection result = userService.query(query);
        return userDtoMapper.mapToDto(result);
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    void createUser(@RequestBody @Valid CreateUserDto createUserDto) {
        userService.dispatch(userDtoMapper.mapToCommand(createUserDto));
    }

    @PatchMapping("/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    void updateUser(@PathVariable UUID id, @RequestBody @Valid UpdateUserDto updateUserDto) {
        userService.dispatch(userDtoMapper.mapToCommand(id, updateUserDto));
    }

    @GetMapping("/users")
    PageDto<UserDto> getAllUsers(@RequestParam(required = false, defaultValue = "0") @Min(0) int page,
                                 @RequestParam(required = false, defaultValue = "25") @Min(0) int perPage) {
        return userDtoMapper.mapToDTO(userService.query(FindAllUsersQuery.builder()
                .page(page)
                .perPage(perPage)
                .build()
        ));
    }

    @GetMapping("/users/{id}")
    UserDto getUserById(@PathVariable UUID id) {
        return userDtoMapper.mapToDto(userService.query(new FindUserByIdQuery(id)));
    }

    @DeleteMapping("/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    void deleteUserById(@PathVariable UUID id) {
        userService.dispatch(new DeleteUserByIdCommand(id));
    }
}
