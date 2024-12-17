package de.mueller_constantin.taskcare.api.core.user.application.service;

import de.mueller_constantin.taskcare.api.core.common.application.NoSuchEntityException;
import de.mueller_constantin.taskcare.api.core.common.application.persistence.MediaStorage;
import de.mueller_constantin.taskcare.api.core.common.domain.Page;
import de.mueller_constantin.taskcare.api.core.common.domain.PageInfo;
import de.mueller_constantin.taskcare.api.core.user.application.*;
import de.mueller_constantin.taskcare.api.core.user.application.persistence.UserDomainRepository;
import de.mueller_constantin.taskcare.api.core.user.application.persistence.UserStateRepository;
import de.mueller_constantin.taskcare.api.core.user.application.security.CredentialsEncoder;
import de.mueller_constantin.taskcare.api.core.user.domain.IdentityProvider;
import de.mueller_constantin.taskcare.api.core.user.domain.Role;
import de.mueller_constantin.taskcare.api.core.user.domain.UserAggregate;
import de.mueller_constantin.taskcare.api.core.user.domain.UserProjection;
import jakarta.validation.Validator;
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
    private UserDomainRepository userDomainRepository;

    @Mock
    private UserStateRepository userStateRepository;

    @Mock
    private CredentialsEncoder credentialsEncoder;

    @Mock
    private MediaStorage mediaStorage;

    @Mock
    private Validator validator;

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
                "Abc123", "Maximilian Mustermann", Role.USER, IdentityProvider.LOCAL);

        this.userProjection = UserProjection.builder()
                .id(this.id)
                .username("maxi123")
                .password("Abc123")
                .displayName("Maximilian Mustermann")
                .role(Role.USER)
                .identityProvider(IdentityProvider.LOCAL)
                .locked(false)
                .build();
    }

    @Test
    void handleCreateUserCommand() {
        when(userStateRepository.existsByUsername("erika123")).thenReturn(false);
        when(credentialsEncoder.encode(any())).thenAnswer(i -> i.getArguments()[0].toString());
        doNothing().when(userDomainRepository).save(any(UserAggregate.class));

        userService.dispatch(CreateUserCommand.builder()
                .username("erika123")
                .password("Abc123")
                .displayName("Erika Musterfrau")
                .role(Role.USER)
                .build());

        verify(userStateRepository, times(1)).existsByUsername("erika123");
        verify(credentialsEncoder, times(1)).encode("Abc123");
        verify(userDomainRepository, times(1)).save(any(UserAggregate.class));
    }

    @Test
    void handleCreateUserCommandWithUsernameAlreadyInUse() {
        when(userStateRepository.existsByUsername("maxi123")).thenReturn(true);

        assertThrows(UsernameAlreadyInUseException.class, () -> {
            userService.dispatch(CreateUserCommand.builder()
                    .username("maxi123")
                    .password("Abc123")
                    .displayName("Maximilian Mustermann")
                    .role(Role.USER)
                    .build());
        });
    }

    @Test
    void handleUpdateUserByIdCommand() {
        when(userStateRepository.findById(id)).thenReturn(Optional.of(userProjection));
        when(userDomainRepository.load(id)).thenReturn(Optional.of(userAggregate));
        when(credentialsEncoder.encode(any())).thenAnswer(i -> i.getArguments()[0].toString());
        doNothing().when(userDomainRepository).save(any(UserAggregate.class));

        userService.dispatch(UpdateUserByIdCommand.builder()
                .id(id)
                .password("Abc123")
                .displayName("Erika Musterfrau")
                .role(Role.USER)
                .build());

        verify(userStateRepository, times(1)).findById(id);
        verify(userDomainRepository, times(1)).load(id);
        verify(credentialsEncoder, times(1)).encode("Abc123");
        verify(userDomainRepository, times(1)).save(any(UserAggregate.class));
    }

    @Test
    void handleUpdateUserByIdCommandUnknownId() {
        when(userStateRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NoSuchEntityException.class, () -> {
            userService.dispatch(UpdateUserByIdCommand.builder()
                    .id(id)
                    .password("Def456")
                    .displayName("Max Mustermann")
                    .role(Role.USER)
                    .build());
        });
    }

    @Test
    void handleDeleteUserByIdCommand() {
        when(userDomainRepository.load(id)).thenReturn(Optional.of(userAggregate));
        doNothing().when(userDomainRepository).save(any(UserAggregate.class));
        when(mediaStorage.exists(any())).thenReturn(true);
        doNothing().when(mediaStorage).delete(any());

        userService.dispatch(DeleteUserByIdCommand.builder()
                .id(id)
                .build());

        verify(userDomainRepository, times(1)).load(id);
        verify(userDomainRepository, times(1)).save(any(UserAggregate.class));
        verify(mediaStorage, times(1)).exists(any());
        verify(mediaStorage, times(1)).delete(any());
    }

    @Test
    void handleDeleteUserByIdCommandUnknownId() {
        when(userDomainRepository.load(id)).thenReturn(Optional.empty());

        assertThrows(NoSuchEntityException.class, () -> {
            userService.dispatch(DeleteUserByIdCommand.builder()
                    .id(id)
                    .build());
        });
    }

    @Test
    void handleLockUserByIdCommand() {
        when(userDomainRepository.load(id)).thenReturn(Optional.of(userAggregate));
        doNothing().when(userDomainRepository).save(any(UserAggregate.class));

        userService.dispatch(LockUserByIdCommand.builder()
                .id(id)
                .build());

        verify(userDomainRepository, times(1)).load(id);
        verify(userDomainRepository, times(1)).save(any(UserAggregate.class));
    }

    @Test
    void handleUnlockUserByIdCommand() {
        when(userDomainRepository.load(id)).thenReturn(Optional.of(userAggregate));
        doNothing().when(userDomainRepository).save(any(UserAggregate.class));

        userService.dispatch(UnlockUserByIdCommand.builder()
                .id(id)
                .build());

        verify(userDomainRepository, times(1)).load(id);
        verify(userDomainRepository, times(1)).save(any(UserAggregate.class));
    }

    @Test
    void handleLockUserByIdCommandUnknownId() {
        when(userDomainRepository.load(id)).thenReturn(Optional.empty());

        assertThrows(NoSuchEntityException.class, () -> {
            userService.dispatch(LockUserByIdCommand.builder()
                    .id(id)
                    .build());
        });
    }

    @Test
    void handleUnlockUserByIdCommandUnknownId() {
        when(userDomainRepository.load(id)).thenReturn(Optional.empty());

        assertThrows(NoSuchEntityException.class, () -> {
            userService.dispatch(UnlockUserByIdCommand.builder()
                    .id(id)
                    .build());
        });
    }

    @Test
    void handleFindUserByIdQuery() {
        when(userStateRepository.findById(id)).thenReturn(Optional.of(userProjection));

        UserProjection result = userService.query(FindUserByIdQuery.builder()
                .id(id)
                .build());

        assertEquals(userProjection, result);
    }

    @Test
    void handleFindUserByIdQueryUnknownId() {
        when(userStateRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NoSuchEntityException.class, () -> {
            userService.query(FindUserByIdQuery.builder()
                    .id(id)
                    .build());
        });
    }

    @Test
    void handleFindUserByUsernameQuery() {
        when(userStateRepository.findByUsername(userAggregate.getUsername())).thenReturn(Optional.of(userProjection));

        UserProjection result = userService.query(FindUserByUsernameQuery.builder()
                .username(userAggregate.getUsername())
                .build());

        assertEquals(userProjection, result);
    }

    @Test
    void handleFindUserByUsernameQueryUnknownUsername() {
        when(userStateRepository.findByUsername("erika123")).thenReturn(Optional.empty());

        assertThrows(NoSuchEntityException.class, () -> {
            userService.query(FindUserByUsernameQuery.builder()
                    .username("erika123")
                    .build());
        });
    }

    @Test
    void handleFindAllUsersQuery() {
        when(userStateRepository.findAll(any(PageInfo.class))).thenReturn(Page.<UserProjection>builder()
                .content(List.of(userProjection))
                .info(PageInfo.builder()
                        .page(0)
                        .perPage(10)
                        .build())
                .build());

        Page<UserProjection> result = userService.query(FindAllUsersQuery.builder()
                .page(0)
                .perPage(10)
                .build());

        assertEquals(userProjection, result.getContent().get(0));
        verify(userStateRepository, times(1)).findAll(any(PageInfo.class));
    }
}