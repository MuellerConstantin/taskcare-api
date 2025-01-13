package de.mueller_constantin.taskcare.api.core.user.application;

import de.mueller_constantin.taskcare.api.core.common.application.NoSuchEntityException;
import de.mueller_constantin.taskcare.api.core.common.application.event.DomainEventBus;
import de.mueller_constantin.taskcare.api.core.common.application.persistence.MediaStorage;
import de.mueller_constantin.taskcare.api.core.user.application.command.*;
import de.mueller_constantin.taskcare.api.core.user.application.persistence.UserEventStoreRepository;
import de.mueller_constantin.taskcare.api.core.user.application.persistence.UserReadModelRepository;
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

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserWriteServiceTest {
    @Mock
    private UserEventStoreRepository userEventStoreRepository;

    @Mock
    private UserReadModelRepository userReadModelRepository;

    @Mock
    private CredentialsEncoder credentialsEncoder;

    @Mock
    private MediaStorage mediaStorage;

    @Mock
    private DomainEventBus domainEventBus;

    @Mock
    private Validator validator;

    @InjectMocks
    private UserWriteService userWriteService;

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
        when(userReadModelRepository.existsByUsername("erika123")).thenReturn(false);
        when(credentialsEncoder.encode(any())).thenAnswer(i -> i.getArguments()[0].toString());
        doNothing().when(userEventStoreRepository).save(any(UserAggregate.class));

        userWriteService.dispatch(CreateUserCommand.builder()
                .username("erika123")
                .password("Abc123")
                .displayName("Erika Musterfrau")
                .role(Role.USER)
                .build());

        verify(userReadModelRepository, times(1)).existsByUsername("erika123");
        verify(credentialsEncoder, times(1)).encode("Abc123");
        verify(userEventStoreRepository, times(1)).save(any(UserAggregate.class));
    }

    @Test
    void handleCreateUserCommandWithUsernameAlreadyInUse() {
        when(userReadModelRepository.existsByUsername("maxi123")).thenReturn(true);

        assertThrows(UsernameAlreadyInUseException.class, () -> {
            userWriteService.dispatch(CreateUserCommand.builder()
                    .username("maxi123")
                    .password("Abc123")
                    .displayName("Maximilian Mustermann")
                    .role(Role.USER)
                    .build());
        });
    }

    @Test
    void handleUpdateUserByIdCommand() {
        when(userReadModelRepository.findById(id)).thenReturn(Optional.of(userProjection));
        when(userEventStoreRepository.load(id)).thenReturn(Optional.of(userAggregate));
        when(credentialsEncoder.encode(any())).thenAnswer(i -> i.getArguments()[0].toString());
        doNothing().when(userEventStoreRepository).save(any(UserAggregate.class));

        userWriteService.dispatch(UpdateUserByIdCommand.builder()
                .id(id)
                .password("Abc123")
                .displayName("Erika Musterfrau")
                .role(Role.USER)
                .build());

        verify(userReadModelRepository, times(1)).findById(id);
        verify(userEventStoreRepository, times(1)).load(id);
        verify(credentialsEncoder, times(1)).encode("Abc123");
        verify(userEventStoreRepository, times(1)).save(any(UserAggregate.class));
    }

    @Test
    void handleUpdateUserByIdCommandUnknownId() {
        when(userReadModelRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NoSuchEntityException.class, () -> {
            userWriteService.dispatch(UpdateUserByIdCommand.builder()
                    .id(id)
                    .password("Def456")
                    .displayName("Max Mustermann")
                    .role(Role.USER)
                    .build());
        });
    }

    @Test
    void handleDeleteUserByIdCommand() {
        when(userEventStoreRepository.load(id)).thenReturn(Optional.of(userAggregate));
        doNothing().when(userEventStoreRepository).save(any(UserAggregate.class));

        userWriteService.dispatch(DeleteUserByIdCommand.builder()
                .id(id)
                .build());

        verify(userEventStoreRepository, times(1)).load(id);
        verify(userEventStoreRepository, times(1)).save(any(UserAggregate.class));
    }

    @Test
    void handleDeleteUserByIdCommandUnknownId() {
        when(userEventStoreRepository.load(id)).thenReturn(Optional.empty());

        assertThrows(NoSuchEntityException.class, () -> {
            userWriteService.dispatch(DeleteUserByIdCommand.builder()
                    .id(id)
                    .build());
        });
    }

    @Test
    void handleLockUserByIdCommand() {
        when(userEventStoreRepository.load(id)).thenReturn(Optional.of(userAggregate));
        doNothing().when(userEventStoreRepository).save(any(UserAggregate.class));

        userWriteService.dispatch(LockUserByIdCommand.builder()
                .id(id)
                .build());

        verify(userEventStoreRepository, times(1)).load(id);
        verify(userEventStoreRepository, times(1)).save(any(UserAggregate.class));
    }

    @Test
    void handleUnlockUserByIdCommand() {
        when(userEventStoreRepository.load(id)).thenReturn(Optional.of(userAggregate));
        doNothing().when(userEventStoreRepository).save(any(UserAggregate.class));

        userWriteService.dispatch(UnlockUserByIdCommand.builder()
                .id(id)
                .build());

        verify(userEventStoreRepository, times(1)).load(id);
        verify(userEventStoreRepository, times(1)).save(any(UserAggregate.class));
    }

    @Test
    void handleLockUserByIdCommandUnknownId() {
        when(userEventStoreRepository.load(id)).thenReturn(Optional.empty());

        assertThrows(NoSuchEntityException.class, () -> {
            userWriteService.dispatch(LockUserByIdCommand.builder()
                    .id(id)
                    .build());
        });
    }

    @Test
    void handleUnlockUserByIdCommandUnknownId() {
        when(userEventStoreRepository.load(id)).thenReturn(Optional.empty());

        assertThrows(NoSuchEntityException.class, () -> {
            userWriteService.dispatch(UnlockUserByIdCommand.builder()
                    .id(id)
                    .build());
        });
    }
}