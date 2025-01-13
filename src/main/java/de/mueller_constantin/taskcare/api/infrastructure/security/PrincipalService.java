package de.mueller_constantin.taskcare.api.infrastructure.security;

import de.mueller_constantin.taskcare.api.core.common.application.NoSuchEntityException;
import de.mueller_constantin.taskcare.api.core.user.application.UserReadService;
import de.mueller_constantin.taskcare.api.core.user.application.query.FindUserByUsernameQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Primary
public class PrincipalService implements UserDetailsService {
    private final UserReadService userReadService;

    @Autowired
    public PrincipalService(UserReadService userReadService) {
        this.userReadService = userReadService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            return new Principal(this.userReadService.query(new FindUserByUsernameQuery(username)));
        } catch(NoSuchEntityException exc) {
            throw new UsernameNotFoundException("User not found", exc);
        }
    }
}
