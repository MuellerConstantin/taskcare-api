package de.x1c1b.taskcare.service.presentation.rest.v1;

import de.x1c1b.taskcare.service.core.common.domain.Page;
import de.x1c1b.taskcare.service.core.user.application.UserService;
import de.x1c1b.taskcare.service.core.user.application.command.CreateUserCommand;
import de.x1c1b.taskcare.service.core.user.application.command.DeleteUserByUsernameCommand;
import de.x1c1b.taskcare.service.core.user.application.command.UpdateUserByUsernameCommand;
import de.x1c1b.taskcare.service.core.user.application.query.FindAllUsersQuery;
import de.x1c1b.taskcare.service.core.user.application.query.FindUserByUsernameQuery;
import de.x1c1b.taskcare.service.core.user.domain.User;
import de.x1c1b.taskcare.service.presentation.rest.v1.dto.CreateUserDTO;
import de.x1c1b.taskcare.service.presentation.rest.v1.dto.PageDTO;
import de.x1c1b.taskcare.service.presentation.rest.v1.dto.UpdateUserDTO;
import de.x1c1b.taskcare.service.presentation.rest.v1.dto.UserDTO;
import de.x1c1b.taskcare.service.presentation.rest.v1.dto.mapper.UserDTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;

@RestController
@RequestMapping("/v1/users")
@Validated
public class UserController {

    private final UserService userService;
    private final UserDTOMapper userDTOMapper;

    @Autowired
    public UserController(UserService userService, UserDTOMapper userDTOMapper) {
        this.userService = userService;
        this.userDTOMapper = userDTOMapper;
    }

    @GetMapping
    PageDTO<UserDTO> findAll(@RequestParam(required = false, defaultValue = "0") @Min(0) int page,
                             @RequestParam(required = false, defaultValue = "25") @Min(0) int perPage) {
        FindAllUsersQuery query = new FindAllUsersQuery(page, perPage);
        Page<User> result = userService.query(query);
        return userDTOMapper.mapToDTO(result);
    }

    @GetMapping("/{username}")
    UserDTO findByUsername(@PathVariable("username") String username) {
        FindUserByUsernameQuery query = new FindUserByUsernameQuery(username);
        User result = userService.query(query);
        return userDTOMapper.mapToDTO(result);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    void create(@RequestBody CreateUserDTO dto) {
        CreateUserCommand command = userDTOMapper.mapToCommand(dto);
        userService.execute(command);
    }

    @PatchMapping("/{username}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void updateByUsername(@PathVariable("username") String username, @RequestBody UpdateUserDTO dto) {
        UpdateUserByUsernameCommand command = userDTOMapper.mapToCommand(username, dto);
        userService.execute(command);
    }

    @DeleteMapping("/{username}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteByUsername(@PathVariable("username") String username) {
        DeleteUserByUsernameCommand command = new DeleteUserByUsernameCommand(username);
        userService.execute(command);
    }
}
