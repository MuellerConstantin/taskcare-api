package de.mueller_constantin.taskcare.api.infrastructure.security.ldap;

import de.mueller_constantin.taskcare.api.core.user.application.UserReadService;
import de.mueller_constantin.taskcare.api.core.user.application.query.FindUserByUsernameQuery;
import de.mueller_constantin.taskcare.api.core.user.application.command.SyncLdapUserCommand;
import de.mueller_constantin.taskcare.api.core.user.application.UserWriteService;
import de.mueller_constantin.taskcare.api.infrastructure.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;

import java.util.Collection;

@RequiredArgsConstructor
public class LdapUserContextMapper implements UserDetailsContextMapper {
    private final UserWriteService userWriteService;
    private final UserReadService userReadService;
    private final LdapUserMapper ldapUserMapper;

    @Override
    public UserDetails mapUserFromContext(DirContextOperations ctx, String username, Collection<? extends GrantedAuthority> authorities) {
        LdapUser ldapUser = ldapUserMapper.mapFromContext(ctx);

        this.userWriteService.dispatch(SyncLdapUserCommand.builder()
                .username(ldapUser.getUsername())
                .displayName(ldapUser.getDisplayName())
                .build());

        return new Principal(this.userReadService.query(new FindUserByUsernameQuery(ldapUser.getUsername())));
    }

    @Override
    public void mapUserToContext(UserDetails user, DirContextAdapter ctx) {
        throw new UnsupportedOperationException("LdapPersonContextMapper only supports reading from a context. Please "
                + "use a subclass if mapUserToContext() is required.");
    }
}
