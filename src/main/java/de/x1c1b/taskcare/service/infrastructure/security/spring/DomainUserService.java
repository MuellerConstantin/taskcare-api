package de.x1c1b.taskcare.service.infrastructure.security.spring;

import de.x1c1b.taskcare.service.core.common.application.EntityNotFoundException;
import de.x1c1b.taskcare.service.core.user.application.UserService;
import de.x1c1b.taskcare.service.core.user.application.query.FindUserByUsernameQuery;
import de.x1c1b.taskcare.service.core.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DomainUserService implements UserDetailsService {

    private final UserService userService;

    @Autowired
    public DomainUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        FindUserByUsernameQuery query = new FindUserByUsernameQuery(username);

        try {
            User user = userService.query(query);
            return new org.springframework.security.core.userdetails.User(user.getUsername(),
                    user.getPassword(),
                    user.isEnabled(),
                    true,
                    true,
                    !user.isLocked(),
                    Set.of());
        } catch (EntityNotFoundException exc) {
            throw new UsernameNotFoundException("Username not found", exc);
        }
    }
}
