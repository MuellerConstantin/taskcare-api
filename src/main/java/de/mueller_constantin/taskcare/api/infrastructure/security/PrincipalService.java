package de.mueller_constantin.taskcare.api.infrastructure.security;

import de.mueller_constantin.taskcare.api.core.common.application.NoSuchEntityException;
import de.mueller_constantin.taskcare.api.core.user.application.FindUserByUsernameQuery;
import de.mueller_constantin.taskcare.api.core.user.application.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Primary
public class PrincipalService implements UserDetailsService {
    private final UserService userService;

    @Autowired
    public PrincipalService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            return new Principal(this.userService.query(new FindUserByUsernameQuery(username)));
        } catch(NoSuchEntityException exc) {
            throw new UsernameNotFoundException("User not found", exc);
        }
    }
}
