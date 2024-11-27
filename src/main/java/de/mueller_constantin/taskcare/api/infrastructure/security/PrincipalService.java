package de.mueller_constantin.taskcare.api.infrastructure.security;

import de.mueller_constantin.taskcare.api.core.user.application.repository.UserProjectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Primary
public class PrincipalService implements UserDetailsService {
    private final UserProjectionRepository userProjectionRepository;

    @Autowired
    public PrincipalService(UserProjectionRepository userProjectionRepository) {
        this.userProjectionRepository = userProjectionRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userProjectionRepository.findByUsername(username).map(Principal::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
