package de.mueller_constantin.taskcare.api.infrastructure.security.ldap;

import lombok.RequiredArgsConstructor;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.DirContextOperations;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

@RequiredArgsConstructor
public class LdapUserMapper implements AttributesMapper<LdapUser> {
    private final LdapSecurityProperties ldapSecurityProperties;

    @Override
    public LdapUser mapFromAttributes(Attributes attributes) throws NamingException {
        String username = attributes.get(ldapSecurityProperties.getSync().getUsernameField()).get().toString();
        String displayName = attributes.get(ldapSecurityProperties.getSync().getDisplayNameField()).get().toString();
        byte[] photo = (byte[]) attributes.get(ldapSecurityProperties.getSync().getPhotoField()).get();

        return new LdapUser(username, displayName, photo);
    }

    public LdapUser mapFromContext(DirContextOperations ctx) {
        String username = ctx.getStringAttribute(ldapSecurityProperties.getSync().getUsernameField());
        String displayName = ctx.getStringAttribute(ldapSecurityProperties.getSync().getDisplayNameField());
        byte[] photo = (byte[]) ctx.getObjectAttribute(ldapSecurityProperties.getSync().getPhotoField());

        return new LdapUser(username, displayName, photo);
    }
}
