package de.mueller_constantin.taskcare.api.core.user.application.service;

import de.mueller_constantin.taskcare.api.core.common.application.service.NoSuchEntityException;
import de.mueller_constantin.taskcare.api.core.common.domain.model.Page;
import de.mueller_constantin.taskcare.api.core.common.domain.model.PageInfo;
import de.mueller_constantin.taskcare.api.core.user.application.repository.UserAggregateRepository;
import de.mueller_constantin.taskcare.api.core.user.application.repository.UserProjectionRepository;
import de.mueller_constantin.taskcare.api.core.user.application.security.CredentialsEncoder;
import de.mueller_constantin.taskcare.api.core.user.domain.model.Role;
import de.mueller_constantin.taskcare.api.core.user.domain.model.UserAggregate;
import de.mueller_constantin.taskcare.api.core.user.domain.model.UserProjection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserAggregateRepository userAggregateRepository;

    @Mock
    private UserProjectionRepository userProjectionRepository;

    @Mock
    private CredentialsEncoder credentialsEncoder;

    @InjectMocks
    private UserService userService;

    private UUID id;
    private UserAggregate userAggregate;
    private UserProjection userProjection;

    @BeforeEach
    void setUp() {
        this.id = UUID.randomUUID();

        this.userAggregate = new UserAggregate(this.id, 0, false);
        this.userAggregate.create("maxi123",
                "Abc123", "Maximilian Mustermann", Role.USER);

        this.userProjection = UserProjection.builder()
                .id(this.id)
                .username("maxi123")
                .password("Abc123")
                .displayName("Maximilian Mustermann")
                .role(Role.USER)
                .locked(false)
                .build();
    }

    @Test
    void handleCreateUserCommand() {
        when(userProjectionRepository.existsByUsername("erika123")).thenReturn(false);
        when(credentialsEncoder.encode(any())).thenAnswer(i -> i.getArguments()[0].toString());
        doNothing().when(userAggregateRepository).save(any(UserAggregate.class));

        userService.handle(CreateUserCommand.builder()
                .username("erika123")
                .password("Abc123")
                .displayName("Erika Musterfrau")
                .role(Role.USER)
                .build());

        verify(userProjectionRepository, times(1)).existsByUsername("erika123");
        verify(credentialsEncoder, times(1)).encode("Abc123");
        verify(userAggregateRepository, times(1)).save(any(UserAggregate.class));
    }

    @Test
    void handleCreateUserCommandWithUsernameAlreadyInUse() {
        when(userProjectionRepository.existsByUsername("maxi123")).thenReturn(true);

        assertThrows(UsernameAlreadyInUseException.class, () -> {
            userService.handle(CreateUserCommand.builder()
                    .username("maxi123")
                    .password("Abc123")
                    .displayName("Maximilian Mustermann")
                    .role(Role.USER)
                    .build());
        });
    }

    @Test
    void handleUpdateUserByIdCommand() {
        when(userProjectionRepository.findById(id)).thenReturn(Optional.of(userProjection));
        when(userAggregateRepository.load(id)).thenReturn(Optional.of(userAggregate));
        when(credentialsEncoder.encode(any())).thenAnswer(i -> i.getArguments()[0].toString());
        doNothing().when(userAggregateRepository).save(any(UserAggregate.class));

        userService.handle(UpdateUserByIdCommand.builder()
                .id(id)
                .password("Abc123")
                .displayName("Erika Musterfrau")
                .role(Role.USER)
                .build());

        verify(userProjectionRepository, times(1)).findById(id);
        verify(userAggregateRepository, times(1)).load(id);
        verify(credentialsEncoder, times(1)).encode("Abc123");
        verify(userAggregateRepository, times(1)).save(any(UserAggregate.class));
    }

    @Test
    void handleUpdateUserByIdCommandUnknownId() {
        when(userProjectionRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NoSuchEntityException.class, () -> {
            userService.handle(UpdateUserByIdCommand.builder()
                    .id(id)
                    .password("Def456")
                    .displayName("Max Mustermann")
                    .role(Role.USER)
                    .build());
        });
    }

    @Test
    void handleDeleteUserByIdCommand() {
        when(userAggregateRepository.load(id)).thenReturn(Optional.of(userAggregate));
        doNothing().when(userAggregateRepository).save(any(UserAggregate.class));

        userService.handle(DeleteUserByIdCommand.builder()
                .id(id)
                .build());

        verify(userAggregateRepository, times(1)).load(id);
        verify(userAggregateRepository, times(1)).save(any(UserAggregate.class));
    }

    @Test
    void handleDeleteByIdCommandUnknownId() {
        when(userAggregateRepository.load(id)).thenReturn(Optional.empty());

        assertThrows(NoSuchEntityException.class, () -> {
            userService.handle(DeleteUserByIdCommand.builder()
                    .id(id)
                    .build());
        });
    }

    @Test
    void handleLockUserByIdCommand() {
        when(userAggregateRepository.load(id)).thenReturn(Optional.of(userAggregate));
        doNothing().when(userAggregateRepository).save(any(UserAggregate.class));

        userService.handle(LockUserByIdCommand.builder()
                .id(id)
                .build());

        verify(userAggregateRepository, times(1)).load(id);
        verify(userAggregateRepository, times(1)).save(any(UserAggregate.class));
    }

    @Test
    void handleUnlockUserByIdCommand() {
        when(userAggregateRepository.load(id)).thenReturn(Optional.of(userAggregate));
        doNothing().when(userAggregateRepository).save(any(UserAggregate.class));

        userService.handle(UnlockUserByIdCommand.builder()
                .id(id)
                .build());

        verify(userAggregateRepository, times(1)).load(id);
        verify(userAggregateRepository, times(1)).save(any(UserAggregate.class));
    }

    @Test
    void handleLockUserByIdCommandUnknownId() {
        when(userAggregateRepository.load(id)).thenReturn(Optional.empty());

        assertThrows(NoSuchEntityException.class, () -> {
            userService.handle(LockUserByIdCommand.builder()
                    .id(id)
                    .build());
        });
    }

    @Test
    void handleUnlockUserByIdCommandUnknownId() {
        when(userAggregateRepository.load(id)).thenReturn(Optional.empty());

        assertThrows(NoSuchEntityException.class, () -> {
            userService.handle(UnlockUserByIdCommand.builder()
                    .id(id)
                    .build());
        });
    }

    @Test
    void handleFindUserByIdQuery() {
        when(userProjectionRepository.findById(id)).thenReturn(Optional.of(userProjection));

        UserProjection result = userService.handle(FindUserByIdQuery.builder()
                .id(id)
                .build());

        assertEquals(userProjection, result);
    }

    @Test
    void handleFindUserByIdQueryUnknownId() {
        when(userProjectionRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NoSuchEntityException.class, () -> {
            userService.handle(FindUserByIdQuery.builder()
                    .id(id)
                    .build());
        });
    }

    @Test
    void handleFindUserByUsernameQuery() {
        when(userProjectionRepository.findByUsername(userAggregate.getUsername())).thenReturn(Optional.of(userProjection));

        UserProjection result = userService.handle(FindUserByUsernameQuery.builder()
                .username(userAggregate.getUsername())
                .build());

        assertEquals(userProjection, result);
    }

    @Test
    void handleFindUserByUsernameQueryUnknownUsername() {
        when(userProjectionRepository.findByUsername("erika123")).thenReturn(Optional.empty());

        assertThrows(NoSuchEntityException.class, () -> {
            userService.handle(FindUserByUsernameQuery.builder()
                    .username("erika123")
                    .build());
        });
    }

    @Test
    void handleFindAllUsersQuery() {
        when(userProjectionRepository.findAll(any(PageInfo.class))).thenReturn(Page.<UserProjection>builder()
                .content(List.of(userProjection))
                .info(PageInfo.builder()
                        .page(0)
                        .perPage(10)
                        .build())
                .build());

        Page<UserProjection> result = userService.handle(FindAllUsersQuery.builder()
                .page(0)
                .perPage(10)
                .build());

        assertEquals(userProjection, result.getContent().get(0));
        verify(userProjectionRepository, times(1)).findAll(any(PageInfo.class));
    }
}