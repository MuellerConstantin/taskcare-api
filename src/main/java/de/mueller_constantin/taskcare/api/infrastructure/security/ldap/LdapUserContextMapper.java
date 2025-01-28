package de.mueller_constantin.taskcare.api.infrastructure.security.ldap;

import de.mueller_constantin.taskcare.api.core.common.application.persistence.MediaStorage;
import de.mueller_constantin.taskcare.api.core.user.application.UserReadService;
import de.mueller_constantin.taskcare.api.core.user.application.query.FindUserByUsernameQuery;
import de.mueller_constantin.taskcare.api.core.user.application.command.SyncLdapUserCommand;
import de.mueller_constantin.taskcare.api.core.user.application.UserWriteService;
import de.mueller_constantin.taskcare.api.core.user.domain.UserProjection;
import de.mueller_constantin.taskcare.api.infrastructure.security.Principal;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;

import java.io.ByteArrayInputStream;
import java.net.URLConnection;
import java.util.Collection;

@RequiredArgsConstructor
public class LdapUserContextMapper implements UserDetailsContextMapper {
    private final UserWriteService userWriteService;
    private final UserReadService userReadService;
    private final MediaStorage mediaStorage;
    private final LdapUserMapper ldapUserMapper;

    @Override
    @SneakyThrows
    public UserDetails mapUserFromContext(DirContextOperations ctx, String username, Collection<? extends GrantedAuthority> authorities) {
        LdapUser ldapUser = ldapUserMapper.mapFromContext(ctx);

        this.userWriteService.dispatch(SyncLdapUserCommand.builder()
                .username(ldapUser.getUsername())
                .displayName(ldapUser.getDisplayName())
                .build());

        UserProjection userProjection = this.userReadService.query(new FindUserByUsernameQuery(ldapUser.getUsername()));

        if(ldapUser.getPhoto() != null) {
            String contentType = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(ldapUser.getPhoto()));

            mediaStorage.save("/profile-images/" + userProjection.getId().toString(),
                    contentType != null ? contentType : "application/octet-stream", ldapUser.getPhoto());
        } else {
            mediaStorage.delete("/profile-images/" + userProjection.getId().toString());
        }

        return new Principal(userProjection);
    }

    @Override
    public void mapUserToContext(UserDetails user, DirContextAdapter ctx) {
        throw new UnsupportedOperationException("LdapPersonContextMapper only supports reading from a context. Please "
                + "use a subclass if mapUserToContext() is required.");
    }
}
