package de.mueller_constantin.taskcare.api.core.user.application;

import de.mueller_constantin.taskcare.api.core.common.application.ApplicationService;
import de.mueller_constantin.taskcare.api.core.common.application.NoSuchEntityException;
import de.mueller_constantin.taskcare.api.core.common.application.persistence.MediaStorage;
import de.mueller_constantin.taskcare.api.core.common.domain.Page;
import de.mueller_constantin.taskcare.api.core.common.domain.PageInfo;
import de.mueller_constantin.taskcare.api.core.user.application.persistence.UserEventStoreRepository;
import de.mueller_constantin.taskcare.api.core.user.application.persistence.UserReadModelRepository;
import de.mueller_constantin.taskcare.api.core.user.application.security.CredentialsEncoder;
import de.mueller_constantin.taskcare.api.core.user.domain.IdentityProvider;
import de.mueller_constantin.taskcare.api.core.user.domain.Role;
import de.mueller_constantin.taskcare.api.core.user.domain.UserAggregate;
import de.mueller_constantin.taskcare.api.core.user.domain.UserProjection;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
public class UserService implements ApplicationService {
    private final UserEventStoreRepository userEventStoreRepository;
    private final UserReadModelRepository userReadModelRepository;
    private final CredentialsEncoder credentialsEncoder;
    private final MediaStorage mediaStorage;
    private final Validator validator;

    protected void validate(Object object) throws ConstraintViolationException {
        Set<ConstraintViolation<Object>> violations = validator.validate(object);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    public void dispatch(CreateUserCommand command) {
        validate(command);

        boolean usernameInUse = userReadModelRepository.existsByUsername(command.getUsername());

        if (usernameInUse) {
            throw new UsernameAlreadyInUseException();
        }

        UserAggregate userAggregate = new UserAggregate();

        String hashedPassword = credentialsEncoder.encode(command.getPassword());

        userAggregate.create(command.getUsername(), hashedPassword, command.getDisplayName(), command.getRole(), command.getIdentityProvider());
        userEventStoreRepository.save(userAggregate);
    }

    public void dispatch(SyncDefaultAdminCommand command) {
        validate(command);

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
    }

    public void dispatch(SyncLdapUserCommand command) {
        validate(command);

        Optional<UserProjection> userProjection = userReadModelRepository.findByUsername(command.getUsername());

        if(userProjection.isPresent() && userProjection.get().getIdentityProvider() != IdentityProvider.LDAP) {
            throw new UsernameAlreadyInUseException();
        }

        if(userProjection.isEmpty()) {
            UserAggregate userAggregate = new UserAggregate();

            userAggregate.create(command.getUsername(), null, command.getDisplayName(), Role.USER, IdentityProvider.LDAP);
            userEventStoreRepository.save(userAggregate);
        } else {
            if(!userProjection.get().getDisplayName().equals(command.getDisplayName())) {
                UserAggregate userAggregate = userEventStoreRepository.load(userProjection.get().getId())
                        .orElseThrow(NoSuchEntityException::new);

                userAggregate.update(null, command.getDisplayName(), userProjection.get().getRole());
                userEventStoreRepository.save(userAggregate);
            }
        }
    }

    public void dispatch(UpdateUserByIdCommand command) {
        validate(command);

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
    }

    public void dispatch(DeleteUserByIdCommand command) {
        validate(command);

        UserAggregate userAggregate = userEventStoreRepository.load(command.getId())
                .orElseThrow(NoSuchEntityException::new);

        if(userAggregate.getUsername().equals(UserAggregate.DEFAULT_ADMIN_USERNAME)) {
            throw new IllegalDefaultAdminAlterationException("Cannot delete default admin user");
        }

        if(mediaStorage.exists("/profile-images/" + userAggregate.getId().toString())) {
            mediaStorage.delete("/profile-images/" + userAggregate.getId().toString());
        }

        userAggregate.delete();
        userEventStoreRepository.save(userAggregate);
    }

    public void dispatch(LockUserByIdCommand command) {
        validate(command);

        UserAggregate userAggregate = userEventStoreRepository.load(command.getId())
                .orElseThrow(NoSuchEntityException::new);

        if(userAggregate.getUsername().equals(UserAggregate.DEFAULT_ADMIN_USERNAME)) {
            throw new IllegalDefaultAdminAlterationException("Cannot lock default admin user");
        }

        userAggregate.lock();
        userEventStoreRepository.save(userAggregate);
    }

    public void dispatch(UnlockUserByIdCommand command) {
        validate(command);

        UserAggregate userAggregate = userEventStoreRepository.load(command.getId())
                .orElseThrow(NoSuchEntityException::new);

        if(userAggregate.getUsername().equals(UserAggregate.DEFAULT_ADMIN_USERNAME)) {
            throw new IllegalDefaultAdminAlterationException("Cannot unlock default admin user");
        }

        userAggregate.unlock();
        userEventStoreRepository.save(userAggregate);
    }

    public UserProjection query(FindUserByIdQuery query) {
        return userReadModelRepository.findById(query.getId())
                .orElseThrow(NoSuchEntityException::new);
    }

    public UserProjection query(FindUserByUsernameQuery query) {
        return userReadModelRepository.findByUsername(query.getUsername())
                .orElseThrow(NoSuchEntityException::new);
    }

    public Page<UserProjection> query(FindAllUsersQuery query) {
        return userReadModelRepository.findAll(PageInfo.builder()
                .page(query.getPage())
                .perPage(query.getPerPage())
                .build());
    }

    public boolean query(ExistsUserByIdQuery query) {
        return userReadModelRepository.existsById(query.getId());
    }
}
