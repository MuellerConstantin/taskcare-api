package de.mueller_constantin.taskcare.api.core.user.application;

import de.mueller_constantin.taskcare.api.core.common.application.ApplicationService;
import de.mueller_constantin.taskcare.api.core.common.application.NoSuchEntityException;
import de.mueller_constantin.taskcare.api.core.common.application.persistence.MediaStorage;
import de.mueller_constantin.taskcare.api.core.common.domain.Page;
import de.mueller_constantin.taskcare.api.core.common.domain.PageInfo;
import de.mueller_constantin.taskcare.api.core.user.application.persistence.UserDomainRepository;
import de.mueller_constantin.taskcare.api.core.user.application.persistence.UserStateRepository;
import de.mueller_constantin.taskcare.api.core.user.application.security.CredentialsEncoder;
import de.mueller_constantin.taskcare.api.core.user.domain.IdentityProvider;
import de.mueller_constantin.taskcare.api.core.user.domain.Role;
import de.mueller_constantin.taskcare.api.core.user.domain.UserAggregate;
import de.mueller_constantin.taskcare.api.core.user.domain.UserProjection;
import lombok.RequiredArgsConstructor;

import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
public class UserService implements ApplicationService {
    private final UserDomainRepository userAggregateRepository;
    private final UserStateRepository userProjectionRepository;
    private final CredentialsEncoder credentialsEncoder;
    private final MediaStorage mediaStorage;

    public void dispatch(CreateUserCommand command) {
        boolean usernameInUse = userProjectionRepository.existsByUsername(command.getUsername());

        if (usernameInUse) {
            throw new UsernameAlreadyInUseException();
        }

        UserAggregate userAggregate = new UserAggregate();

        String hashedPassword = credentialsEncoder.encode(command.getPassword());

        userAggregate.create(command.getUsername(), hashedPassword, command.getDisplayName(), command.getRole(), command.getIdentityProvider());
        userAggregateRepository.save(userAggregate);
    }

    public void dispatch(SyncDefaultAdminCommand command) {
        boolean defaultAdminExists = userProjectionRepository.existsByUsername(UserAggregate.DEFAULT_ADMIN_USERNAME);

        if(!defaultAdminExists) {
            UserAggregate userAggregate = new UserAggregate();

            String hashedPassword = credentialsEncoder.encode(command.getPassword());

            userAggregate.create(UserAggregate.DEFAULT_ADMIN_USERNAME, hashedPassword, null, Role.ADMINISTRATOR, IdentityProvider.LOCAL);
            userAggregateRepository.save(userAggregate);
        } else {
            UserProjection userProjection = userProjectionRepository.findByUsername(UserAggregate.DEFAULT_ADMIN_USERNAME)
                    .orElseThrow(NoSuchEntityException::new);

            if(!credentialsEncoder.matches(command.getPassword(), userProjection.getPassword())) {
                String hashedPassword = credentialsEncoder.encode(command.getPassword());

                UserAggregate userAggregate = userAggregateRepository.load(userProjection.getId())
                        .orElseThrow(NoSuchEntityException::new);

                userAggregate.update(hashedPassword, null, Role.ADMINISTRATOR);
                userAggregateRepository.save(userAggregate);
            }
        }
    }

    public void dispatch(SyncLdapUserCommand command) {
        Optional<UserProjection> userProjection = userProjectionRepository.findByUsername(command.getUsername());

        if(userProjection.isPresent() && userProjection.get().getIdentityProvider() != IdentityProvider.LDAP) {
            throw new UsernameAlreadyInUseException();
        }

        if(userProjection.isEmpty()) {
            UserAggregate userAggregate = new UserAggregate();

            userAggregate.create(command.getUsername(), null, command.getDisplayName(), Role.USER, IdentityProvider.LDAP);
            userAggregateRepository.save(userAggregate);
        } else {
            if(!userProjection.get().getDisplayName().equals(command.getDisplayName())) {
                UserAggregate userAggregate = userAggregateRepository.load(userProjection.get().getId())
                        .orElseThrow(NoSuchEntityException::new);

                userAggregate.update(null, command.getDisplayName(), userProjection.get().getRole());
                userAggregateRepository.save(userAggregate);
            }
        }
    }

    public void dispatch(UpdateUserByIdCommand command) {
        UserProjection userProjection = userProjectionRepository.findById(command.getId())
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

        UserAggregate userAggregate = userAggregateRepository.load(command.getId())
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
        userAggregateRepository.save(userAggregate);
    }

    public void dispatch(DeleteUserByIdCommand command) {
        UserAggregate userAggregate = userAggregateRepository.load(command.getId())
                .orElseThrow(NoSuchEntityException::new);

        if(userAggregate.getUsername().equals(UserAggregate.DEFAULT_ADMIN_USERNAME)) {
            throw new IllegalDefaultAdminAlterationException("Cannot delete default admin user");
        }

        if(mediaStorage.exists("/profile-images/" + userAggregate.getId().toString())) {
            mediaStorage.delete("/profile-images/" + userAggregate.getId().toString());
        }

        userAggregate.delete();
        userAggregateRepository.save(userAggregate);
    }

    public void dispatch(LockUserByIdCommand command) {
        UserAggregate userAggregate = userAggregateRepository.load(command.getId())
                .orElseThrow(NoSuchEntityException::new);

        if(userAggregate.getUsername().equals(UserAggregate.DEFAULT_ADMIN_USERNAME)) {
            throw new IllegalDefaultAdminAlterationException("Cannot lock default admin user");
        }

        userAggregate.lock();
        userAggregateRepository.save(userAggregate);
    }

    public void dispatch(UnlockUserByIdCommand command) {
        UserAggregate userAggregate = userAggregateRepository.load(command.getId())
                .orElseThrow(NoSuchEntityException::new);

        if(userAggregate.getUsername().equals(UserAggregate.DEFAULT_ADMIN_USERNAME)) {
            throw new IllegalDefaultAdminAlterationException("Cannot unlock default admin user");
        }

        userAggregate.unlock();
        userAggregateRepository.save(userAggregate);
    }

    public UserProjection query(FindUserByIdQuery query) {
        return userProjectionRepository.findById(query.getId())
                .orElseThrow(NoSuchEntityException::new);
    }

    public UserProjection query(FindUserByUsernameQuery query) {
        return userProjectionRepository.findByUsername(query.getUsername())
                .orElseThrow(NoSuchEntityException::new);
    }

    public Page<UserProjection> query(FindAllUsersQuery query) {
        return userProjectionRepository.findAll(PageInfo.builder()
                .page(query.getPage())
                .perPage(query.getPerPage())
                .build());
    }
}
