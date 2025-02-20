package de.mueller_constantin.taskcare.api.core.user.application;

import de.mueller_constantin.taskcare.api.core.common.application.ApplicationService;
import de.mueller_constantin.taskcare.api.core.common.application.NoSuchEntityException;
import de.mueller_constantin.taskcare.api.core.common.application.event.DomainEventBus;
import de.mueller_constantin.taskcare.api.core.common.application.persistence.MediaStorage;
import de.mueller_constantin.taskcare.api.core.common.domain.DomainEvent;
import de.mueller_constantin.taskcare.api.core.user.application.command.*;
import de.mueller_constantin.taskcare.api.core.user.application.persistence.UserEventStoreRepository;
import de.mueller_constantin.taskcare.api.core.user.application.persistence.UserReadModelRepository;
import de.mueller_constantin.taskcare.api.core.user.application.security.CredentialsEncoder;
import de.mueller_constantin.taskcare.api.core.user.domain.*;
import jakarta.validation.Valid;
import lombok.SneakyThrows;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Objects;
import java.util.Optional;

@Service
@Validated
@Transactional
public class UserWriteService implements ApplicationService {
    private final UserEventStoreRepository userEventStoreRepository;
    private final UserReadModelRepository userReadModelRepository;
    private final CredentialsEncoder credentialsEncoder;
    private final MediaStorage mediaStorage;
    private final DomainEventBus domainEventBus;
    private final LockRegistry lockRegistry;

    public UserWriteService(UserEventStoreRepository userEventStoreRepository,
                            UserReadModelRepository userReadModelRepository,
                            CredentialsEncoder credentialsEncoder,
                            MediaStorage mediaStorage,
                            DomainEventBus domainEventBus,
                            LockRegistry lockRegistry) {
        this.userEventStoreRepository = userEventStoreRepository;
        this.userReadModelRepository = userReadModelRepository;
        this.credentialsEncoder = credentialsEncoder;
        this.mediaStorage = mediaStorage;
        this.domainEventBus = domainEventBus;
        this.lockRegistry = lockRegistry;

        this.domainEventBus.subscribe(UserDeletedEvent.class, this::onUserDeletedEvent);
    }

    public void dispatch(@Valid CreateUserCommand command) {
        boolean usernameInUse = userReadModelRepository.existsByUsername(command.getUsername());

        if (usernameInUse) {
            throw new UsernameAlreadyInUseException();
        }

        UserAggregate userAggregate = new UserAggregate();

        String hashedPassword = credentialsEncoder.encode(command.getPassword());

        userAggregate.create(command.getUsername(), hashedPassword, command.getDisplayName(), command.getRole(), command.getIdentityProvider());
        userEventStoreRepository.save(userAggregate);
    }

    @SneakyThrows
    public void dispatch(@Valid SyncDefaultAdminCommand command) {
        lockRegistry.executeLocked(UserAggregate.DEFAULT_ADMIN_USERNAME, () -> {
            boolean defaultAdminExists = userReadModelRepository.existsByUsername(UserAggregate.DEFAULT_ADMIN_USERNAME);

            if(!defaultAdminExists) {
                UserAggregate userAggregate = new UserAggregate();

                String hashedPassword = credentialsEncoder.encode(command.getPassword());

                userAggregate.create(UserAggregate.DEFAULT_ADMIN_USERNAME, hashedPassword, null, Role.ADMINISTRATOR, IdentityProvider.LOCAL);
                userEventStoreRepository.save(userAggregate);
            } else {
                UserProjection userProjection = userReadModelRepository.findByUsername(UserAggregate.DEFAULT_ADMIN_USERNAME)
                        .orElseThrow(NoSuchEntityException::new);

                if(!credentialsEncoder.matches(command.getPassword(), userProjection.getPassword())) {
                    String hashedPassword = credentialsEncoder.encode(command.getPassword());

                    UserAggregate userAggregate = userEventStoreRepository.load(userProjection.getId())
                            .orElseThrow(NoSuchEntityException::new);

                    userAggregate.update(hashedPassword, null, Role.ADMINISTRATOR);
                    userEventStoreRepository.save(userAggregate);
                }
            }
        });
    }

    @SneakyThrows
    public void dispatch(@Valid SyncLdapUserCommand command) {
        lockRegistry.executeLocked(command.getUsername(), () -> {
            Optional<UserProjection> userProjection = userReadModelRepository.findByUsername(command.getUsername());

            if(userProjection.isPresent() && userProjection.get().getIdentityProvider() != IdentityProvider.LDAP) {
                throw new UsernameAlreadyInUseException();
            }

            if(userProjection.isEmpty()) {
                UserAggregate userAggregate = new UserAggregate();

                userAggregate.create(command.getUsername(), null, command.getDisplayName(), Role.USER, IdentityProvider.LDAP);
                userEventStoreRepository.save(userAggregate);
            } else {
                if(!Objects.equals(userProjection.get().getDisplayName(), command.getDisplayName())) {
                    UserAggregate userAggregate = userEventStoreRepository.load(userProjection.get().getId())
                            .orElseThrow(NoSuchEntityException::new);

                    userAggregate.update(null, command.getDisplayName(), userProjection.get().getRole());
                    userEventStoreRepository.save(userAggregate);
                }
            }
        });
    }

    @SneakyThrows
    public void dispatch(@Valid UpdateUserByIdCommand command) {
        lockRegistry.executeLocked(command.getId().toString(), () -> {
            UserProjection userProjection = userReadModelRepository.findById(command.getId())
                    .orElseThrow(NoSuchEntityException::new);

            if(userProjection.getUsername().equals(UserAggregate.DEFAULT_ADMIN_USERNAME)) {
                throw new IllegalDefaultAdminAlterationException("Cannot change default admin user");
            }

            if(userProjection.getIdentityProvider() != IdentityProvider.LOCAL) {
                if(command.getPassword() != null) {
                    throw new IllegalImportedUserAlterationException("Cannot change password of imported user");
                }

                if(command.getDisplayName() != null) {
                    throw new IllegalImportedUserAlterationException("Cannot change display name of imported user");
                }
            }

            UserAggregate userAggregate = userEventStoreRepository.load(command.getId())
                    .orElseThrow(NoSuchEntityException::new);

            String password = command.getPassword() != null ?
                    credentialsEncoder.encode(command.getPassword()) :
                    userProjection.getPassword();

            String displayName = command.isDisplayNameTouched() ?
                    command.getDisplayName() :
                    userProjection.getDisplayName();

            Role role = command.getRole() != null ?
                    command.getRole() :
                    userProjection.getRole();

            userAggregate.update(password, displayName, role);
            userEventStoreRepository.save(userAggregate);
        });
    }

    @SneakyThrows
    public void dispatch(@Valid DeleteUserByIdCommand command) {
        lockRegistry.executeLocked(command.getId().toString(), () -> {
            UserAggregate userAggregate = userEventStoreRepository.load(command.getId())
                    .orElseThrow(NoSuchEntityException::new);

            if(userAggregate.getUsername().equals(UserAggregate.DEFAULT_ADMIN_USERNAME)) {
                throw new IllegalDefaultAdminAlterationException("Cannot delete default admin user");
            }

            userAggregate.delete();
            userEventStoreRepository.save(userAggregate);
        });
    }

    @SneakyThrows
    public void dispatch(@Valid LockUserByIdCommand command) {
        lockRegistry.executeLocked(command.getId().toString(), () -> {
            UserAggregate userAggregate = userEventStoreRepository.load(command.getId())
                    .orElseThrow(NoSuchEntityException::new);

            if(userAggregate.getUsername().equals(UserAggregate.DEFAULT_ADMIN_USERNAME)) {
                throw new IllegalDefaultAdminAlterationException("Cannot lock default admin user");
            }

            userAggregate.lock();
            userEventStoreRepository.save(userAggregate);
        });
    }

    @SneakyThrows
    public void dispatch(@Valid UnlockUserByIdCommand command) {
        lockRegistry.executeLocked(command.getId().toString(), () -> {
            UserAggregate userAggregate = userEventStoreRepository.load(command.getId())
                    .orElseThrow(NoSuchEntityException::new);

            if(userAggregate.getUsername().equals(UserAggregate.DEFAULT_ADMIN_USERNAME)) {
                throw new IllegalDefaultAdminAlterationException("Cannot unlock default admin user");
            }

            userAggregate.unlock();
            userEventStoreRepository.save(userAggregate);
        });
    }

    protected void onUserDeletedEvent(DomainEvent event) {
        UserDeletedEvent userDeletedEvent = (UserDeletedEvent) event;

        if(mediaStorage.exists("/profile-images/" + userDeletedEvent.getAggregateId().toString())) {
            mediaStorage.delete("/profile-images/" + userDeletedEvent.getAggregateId().toString());
        }
    }
}
