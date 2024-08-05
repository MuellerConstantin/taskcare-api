package de.x1c1b.taskcare.api.presentation.rest.v1;

import de.x1c1b.taskcare.api.core.common.application.validation.NullOrNotEmpty;
import de.x1c1b.taskcare.api.core.common.domain.Page;
import de.x1c1b.taskcare.api.core.user.application.UserService;
import de.x1c1b.taskcare.api.core.user.application.command.CreateUserCommand;
import de.x1c1b.taskcare.api.core.user.application.command.DeleteUserByUsernameCommand;
import de.x1c1b.taskcare.api.core.user.application.command.UpdateUserByUsernameCommand;
import de.x1c1b.taskcare.api.core.user.application.query.FindAllUsersQuery;
import de.x1c1b.taskcare.api.core.user.application.query.FindUserByUsernameQuery;
import de.x1c1b.taskcare.api.core.user.domain.User;
import de.x1c1b.taskcare.api.presentation.rest.v1.dto.CreateUserDTO;
import de.x1c1b.taskcare.api.presentation.rest.v1.dto.PageDTO;
import de.x1c1b.taskcare.api.presentation.rest.v1.dto.UpdateUserDTO;
import de.x1c1b.taskcare.api.presentation.rest.v1.dto.UserDTO;
import de.x1c1b.taskcare.api.presentation.rest.v1.dto.mapper.UserDTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;

@RestController
@RequestMapping("/api/v1")
@Validated
public class UserController {

    private final UserService userService;
    private final UserDTOMapper userDTOMapper;

    @Autowired
    public UserController(UserService userService, UserDTOMapper userDTOMapper) {
        this.userService = userService;
        this.userDTOMapper = userDTOMapper;
    }

    @GetMapping("/users")
    PageDTO<UserDTO> findAll(@RequestParam(required = false, defaultValue = "0") @Min(0) int page,
                             @RequestParam(required = false, defaultValue = "25") @Min(0) int perPage,
                             @RequestParam(required = false) @NullOrNotEmpty String filter) {
        FindAllUsersQuery query = new FindAllUsersQuery(page, perPage, filter);
        Page<User> result = userService.query(query);
        return userDTOMapper.mapToDTO(result);
    }

    @GetMapping("/users/{username}")
    UserDTO findByUsername(@PathVariable("username") String username) {
        FindUserByUsernameQuery query = new FindUserByUsernameQuery(username);
        User result = userService.query(query);
        return userDTOMapper.mapToDTO(result);
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    void create(@RequestBody CreateUserDTO dto) {
        CreateUserCommand command = userDTOMapper.mapToCommand(dto);
        userService.execute(command);
    }

    @PatchMapping("/users/{username}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void updateByUsername(@PathVariable("username") String username, @RequestBody UpdateUserDTO dto) {
        UpdateUserByUsernameCommand command = userDTOMapper.mapToCommand(dto, username);
        userService.execute(command);
    }

    @DeleteMapping("/users/{username}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteByUsername(@PathVariable("username") String username) {
        DeleteUserByUsernameCommand command = new DeleteUserByUsernameCommand(username);
        userService.execute(command);
    }

    @GetMapping("/auth/user")
    UserDTO getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        FindUserByUsernameQuery query = new FindUserByUsernameQuery(userDetails.getUsername());
        User result = userService.query(query);
        return userDTOMapper.mapToDTO(result);
    }
}
