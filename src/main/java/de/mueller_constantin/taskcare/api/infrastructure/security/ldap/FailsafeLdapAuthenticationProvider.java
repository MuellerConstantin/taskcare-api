package de.mueller_constantin.taskcare.api.infrastructure.security.ldap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.authentication.LdapAuthenticator;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;

/**
 * Version of {@link LdapAuthenticationProvider} that allows to continue the
 * authentication process even if the LDAP server is not reachable.
 */
@Slf4j
public class FailsafeLdapAuthenticationProvider extends LdapAuthenticationProvider {
    public FailsafeLdapAuthenticationProvider(LdapAuthenticator authenticator, LdapAuthoritiesPopulator authoritiesPopulator) {
        super(authenticator, authoritiesPopulator);
    }

    public FailsafeLdapAuthenticationProvider(LdapAuthenticator authenticator) {
        super(authenticator);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            return super.authenticate(authentication);
        } catch(InternalAuthenticationServiceException exc) {
            logger.error("LDAP Authentication failed", exc);
            throw new AuthenticationServiceException("LDAP Authentication failed", exc);
        }
    }
}
