package de.mueller_constantin.taskcare.api.core.user.application.service;

import de.mueller_constantin.taskcare.api.core.common.application.service.ApplicationService;
import de.mueller_constantin.taskcare.api.core.common.application.service.NoSuchEntityException;
import de.mueller_constantin.taskcare.api.core.common.domain.model.Page;
import de.mueller_constantin.taskcare.api.core.common.domain.model.PageInfo;
import de.mueller_constantin.taskcare.api.core.user.application.repository.UserAggregateRepository;
import de.mueller_constantin.taskcare.api.core.user.application.repository.UserProjectionRepository;
import de.mueller_constantin.taskcare.api.core.user.application.security.CredentialsEncoder;
import de.mueller_constantin.taskcare.api.core.user.domain.model.IdentityProvider;
import de.mueller_constantin.taskcare.api.core.user.domain.model.Role;
import de.mueller_constantin.taskcare.api.core.user.domain.model.UserAggregate;
import de.mueller_constantin.taskcare.api.core.user.domain.model.UserProjection;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserService implements ApplicationService {
    private final UserAggregateRepository userAggregateRepository;
    private final UserProjectionRepository userProjectionRepository;
    private final CredentialsEncoder credentialsEncoder;

    public void handle(CreateUserCommand command) {
        boolean usernameInUse = userProjectionRepository.existsByUsername(command.getUsername());

        if (usernameInUse) {
            throw new UsernameAlreadyInUseException();
        }

        UserAggregate userAggregate = new UserAggregate();

        String hashedPassword = credentialsEncoder.encode(command.getPassword());

        userAggregate.create(command.getUsername(), hashedPassword, command.getDisplayName(), command.getRole(), command.getIdentityProvider());
        userAggregateRepository.save(userAggregate);
    }

    public void handle(SyncDefaultAdminCommand command) {
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

    public void handle(UpdateUserByIdCommand command) {
        UserProjection userProjection = userProjectionRepository.findById(command.getId())
                .orElseThrow(NoSuchEntityException::new);

        UserAggregate userAggregate = userAggregateRepository.load(command.getId())
                .orElseThrow(NoSuchEntityException::new);

        String password = command.isPasswordTouched() ?
                credentialsEncoder.encode(command.getPassword()) :
                userProjection.getPassword();

        String displayName = command.isDisplayNameTouched() ?
                command.getDisplayName() :
                userProjection.getDisplayName();

        Role role = command.isRoleTouched() ?
                command.getRole() :
                userProjection.getRole();

        userAggregate.update(password, displayName, role);
        userAggregateRepository.save(userAggregate);
    }

    public void handle(DeleteUserByIdCommand command) {
        UserAggregate userAggregate = userAggregateRepository.load(command.getId())
                .orElseThrow(NoSuchEntityException::new);

        userAggregate.delete();
        userAggregateRepository.save(userAggregate);
    }

    public void handle(LockUserByIdCommand command) {
        UserAggregate userAggregate = userAggregateRepository.load(command.getId())
                .orElseThrow(NoSuchEntityException::new);

        userAggregate.lock();
        userAggregateRepository.save(userAggregate);
    }

    public void handle(UnlockUserByIdCommand command) {
        UserAggregate userAggregate = userAggregateRepository.load(command.getId())
                .orElseThrow(NoSuchEntityException::new);

        userAggregate.unlock();
        userAggregateRepository.save(userAggregate);
    }

    public UserProjection handle(FindUserByIdQuery query) {
        return userProjectionRepository.findById(query.getId())
                .orElseThrow(NoSuchEntityException::new);
    }

    public UserProjection handle(FindUserByUsernameQuery query) {
        return userProjectionRepository.findByUsername(query.getUsername())
                .orElseThrow(NoSuchEntityException::new);
    }

    public Page<UserProjection> handle(FindAllUsersQuery query) {
        return userProjectionRepository.findAll(PageInfo.builder()
                .page(query.getPage())
                .perPage(query.getPerPage())
                .build());
    }
}
