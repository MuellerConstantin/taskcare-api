package de.mueller_constantin.taskcare.api.infrastructure.security;

import de.mueller_constantin.taskcare.api.core.user.application.persistence.UserStateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Primary
public class PrincipalService implements UserDetailsService {
    private final UserStateRepository userProjectionRepository;

    @Autowired
    public PrincipalService(UserStateRepository userProjectionRepository) {
        this.userProjectionRepository = userProjectionRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userProjectionRepository.findByUsername(username).map(Principal::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
