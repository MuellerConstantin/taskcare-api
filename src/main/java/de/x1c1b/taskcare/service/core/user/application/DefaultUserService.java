package de.x1c1b.taskcare.service.core.user.application;

import de.x1c1b.taskcare.service.core.common.application.EntityNotFoundException;
import de.x1c1b.taskcare.service.core.common.application.SecretEncoder;
import de.x1c1b.taskcare.service.core.common.domain.Page;
import de.x1c1b.taskcare.service.core.common.domain.PageSettings;
import de.x1c1b.taskcare.service.core.user.application.command.CreateUserCommand;
import de.x1c1b.taskcare.service.core.user.application.command.DeleteUserByUsernameCommand;
import de.x1c1b.taskcare.service.core.user.application.command.UpdateUserByUsernameCommand;
import de.x1c1b.taskcare.service.core.user.application.query.ExistsUserByEmailQuery;
import de.x1c1b.taskcare.service.core.user.application.query.ExistsUserByUsernameQuery;
import de.x1c1b.taskcare.service.core.user.application.query.FindAllUsersQuery;
import de.x1c1b.taskcare.service.core.user.application.query.FindUserByUsernameQuery;
import de.x1c1b.taskcare.service.core.user.domain.User;
import de.x1c1b.taskcare.service.core.user.domain.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

@AllArgsConstructor
@Validated
public class DefaultUserService implements UserService {

    private final UserRepository userRepository;
    private final SecretEncoder secretEncoder;

    @Override
    public User query(FindUserByUsernameQuery query) throws EntityNotFoundException {
        return userRepository.findById(query.getUsername()).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public boolean query(ExistsUserByUsernameQuery query) {
        return userRepository.existsById(query.getUsername());
    }

    @Override
    public boolean query(ExistsUserByEmailQuery query) {
        return userRepository.existsByEmail(query.getEmail());
    }

    @Override
    public Page<User> query(FindAllUsersQuery query) {
        return userRepository.findAll(PageSettings.builder()
                .page(query.getPage())
                .perPage(query.getPerPage())
                .sortBy(query.getSortBy())
                .sortDirection(query.getSortDirection())
                .build());
    }

    @Override
    public void execute(@Valid CreateUserCommand command) {
        User user = User.builder()
                .username(command.getUsername())
                .email(command.getEmail())
                .password(secretEncoder.encodeSecret(command.getPassword()))
                .firstName(command.getFirstName().orElse(null))
                .firstName(command.getLastName().orElse(null))
                .enabled(true)
                .locked(false)
                .build();

        if (userRepository.existsById(user.getUsername())) {
            throw new UsernameAlreadyInUseException();
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyInUseException();
        }

        userRepository.save(user);
    }

    @Override
    public void execute(@Valid UpdateUserByUsernameCommand command) throws EntityNotFoundException {
        User user = userRepository.findById(command.getUsername()).orElseThrow(EntityNotFoundException::new);

        command.getEmail().ifPresent(user::setEmail);
        command.getPassword().ifPresent(password -> user.setPassword(secretEncoder.encodeSecret(password)));

        if (command.getFirstName().isPresent() || command.isFirstNameDirty()) {
            user.setFirstName(command.getFirstName().orElse(null));
        }

        if (command.getLastName().isPresent() || command.isLastNameDirty()) {
            user.setLastName(command.getLastName().orElse(null));
        }

        userRepository.save(user);
    }

    @Override
    public void execute(DeleteUserByUsernameCommand command) throws EntityNotFoundException {
        if (!userRepository.deleteById(command.getUsername())) {
            throw new EntityNotFoundException();
        }
    }
}
