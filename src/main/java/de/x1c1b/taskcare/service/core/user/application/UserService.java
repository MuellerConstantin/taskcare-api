package de.x1c1b.taskcare.service.core.user.application;

import de.x1c1b.taskcare.service.core.common.application.EntityNotFoundException;
import de.x1c1b.taskcare.service.core.common.domain.Page;
import de.x1c1b.taskcare.service.core.user.application.command.CreateUserCommand;
import de.x1c1b.taskcare.service.core.user.application.command.DeleteUserByUsernameCommand;
import de.x1c1b.taskcare.service.core.user.application.command.UpdateUserByUsernameCommand;
import de.x1c1b.taskcare.service.core.user.application.query.ExistsUserByEmailQuery;
import de.x1c1b.taskcare.service.core.user.application.query.ExistsUserByUsernameQuery;
import de.x1c1b.taskcare.service.core.user.application.query.FindAllUsersQuery;
import de.x1c1b.taskcare.service.core.user.application.query.FindUserByUsernameQuery;
import de.x1c1b.taskcare.service.core.user.domain.User;

public interface UserService {

    User query(FindUserByUsernameQuery query) throws EntityNotFoundException;

    boolean query(ExistsUserByUsernameQuery query);

    boolean query(ExistsUserByEmailQuery query);

    Page<User> query(FindAllUsersQuery query);

    void execute(CreateUserCommand command);

    void execute(UpdateUserByUsernameCommand command) throws EntityNotFoundException;

    void execute(DeleteUserByUsernameCommand command) throws EntityNotFoundException;
}
