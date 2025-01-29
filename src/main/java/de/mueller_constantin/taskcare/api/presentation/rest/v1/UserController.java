package de.mueller_constantin.taskcare.api.presentation.rest.v1;

import com.fasterxml.jackson.annotation.JsonView;
import de.mueller_constantin.taskcare.api.core.common.application.NoSuchEntityException;
import de.mueller_constantin.taskcare.api.core.common.application.persistence.MediaStorage;
import de.mueller_constantin.taskcare.api.core.user.application.*;
import de.mueller_constantin.taskcare.api.core.user.application.command.DeleteUserByIdCommand;
import de.mueller_constantin.taskcare.api.core.user.application.query.FindAllUsersQuery;
import de.mueller_constantin.taskcare.api.core.user.application.query.FindUserByIdQuery;
import de.mueller_constantin.taskcare.api.core.user.application.query.FindUserByUsernameQuery;
import de.mueller_constantin.taskcare.api.core.user.domain.UserProjection;
import de.mueller_constantin.taskcare.api.infrastructure.security.CurrentPrincipal;
import de.mueller_constantin.taskcare.api.infrastructure.security.Principal;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.CreateUserDto;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.PageDto;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.UpdateUserDto;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.UserDto;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.mapper.UserDtoMapper;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.search.SearchFilter;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.view.DefaultJsonViews;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    private final UserWriteService userWriteService;
    private final UserReadService userReadService;
    private final UserDtoMapper userDtoMapper;
    private final MediaStorage mediaStorage;

    @Autowired
    public UserController(UserWriteService userWriteService, UserReadService userReadService, UserDtoMapper userDtoMapper, MediaStorage mediaStorage) {
        this.userWriteService = userWriteService;
        this.userReadService = userReadService;
        this.userDtoMapper = userDtoMapper;
        this.mediaStorage = mediaStorage;
    }

    @GetMapping("/user/me")
    @JsonView(DefaultJsonViews.Me.class)
    UserDto getCurrentUser(@CurrentPrincipal Principal principal) {
        FindUserByUsernameQuery query = new FindUserByUsernameQuery(principal.getUsername());
        UserProjection result = userReadService.query(query);
        return userDtoMapper.mapToDto(result);
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    void createUser(@RequestBody @Valid CreateUserDto createUserDto) {
        userWriteService.dispatch(userDtoMapper.mapToCommand(createUserDto));
    }

    @PatchMapping("/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    void updateUser(@PathVariable UUID id, @RequestBody @Valid UpdateUserDto updateUserDto) {
        userWriteService.dispatch(userDtoMapper.mapToCommand(id, updateUserDto));
    }

    @GetMapping("/users")
    PageDto<UserDto> getAllUsers(@RequestParam(required = false, defaultValue = "0") @Min(0) int page,
                                 @RequestParam(required = false, defaultValue = "25") @Min(0) int perPage,
                                 @RequestParam(required = false) @SearchFilter String search) {
        return userDtoMapper.mapToDto(userReadService.query(FindAllUsersQuery.builder()
                .page(page)
                .perPage(perPage)
                .search(search)
                .build()
        ));
    }

    @GetMapping("/users/{id}")
    UserDto getUserById(@PathVariable UUID id) {
        return userDtoMapper.mapToDto(userReadService.query(new FindUserByIdQuery(id)));
    }

    @DeleteMapping("/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    void deleteUserById(@PathVariable UUID id) {
        userWriteService.dispatch(new DeleteUserByIdCommand(id));
    }

    @PostMapping("/user/me/profile-image")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void uploadProfileImage(@CurrentPrincipal Principal principal, @RequestParam("file") MultipartFile file) throws IOException {
        FindUserByUsernameQuery query = new FindUserByUsernameQuery(principal.getUsername());
        UserProjection result = userReadService.query(query);

        mediaStorage.save("/profile-images/" + result.getId().toString(), file.getContentType(), file.getBytes());
    }

    @DeleteMapping("/user/me/profile-image")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void removeProfileImage(@CurrentPrincipal Principal principal) {
        FindUserByUsernameQuery query = new FindUserByUsernameQuery(principal.getUsername());
        UserProjection result = userReadService.query(query);

        mediaStorage.delete("/profile-images/" + result.getId().toString());
    }

    @GetMapping("/user/me/profile-image")
    ResponseEntity<byte[]> getProfileImage(@CurrentPrincipal Principal principal) {
        FindUserByUsernameQuery query = new FindUserByUsernameQuery(principal.getUsername());
        UserProjection result = userReadService.query(query);

        if(mediaStorage.exists("/profile-images/" + result.getId().toString())) {
            String contentType = mediaStorage.contentType("/profile-images/" + result.getId().toString());
            byte[] data = mediaStorage.load("/profile-images/" + result.getId().toString());

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .contentLength(data.length)
                    .body(data);
        } else {
            throw new NoSuchEntityException();
        }
    }

    @GetMapping("/users/{id}/profile-image")
    ResponseEntity<byte[]> getProfileImage(@PathVariable UUID id) {
        UserProjection result = userReadService.query(new FindUserByIdQuery(id));

        if(mediaStorage.exists("/profile-images/" + result.getId().toString())) {
            String contentType = mediaStorage.contentType("/profile-images/" + result.getId().toString());
            byte[] data = mediaStorage.load("/profile-images/" + result.getId().toString());

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .contentLength(data.length)
                    .body(data);
        } else {
            throw new NoSuchEntityException();
        }
    }
}
