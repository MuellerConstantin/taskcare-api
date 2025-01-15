package de.mueller_constantin.taskcare.api.core.user.domain;

import de.mueller_constantin.taskcare.api.core.common.domain.ValueObject;

/**
 * The identity provider indicates the source of the user account. In the
 * most basic case, the identity provider is the local database. Hence, the
 * user was created using the management console. But it could also be
 * imported from an external source like LDAP.
 */
public enum IdentityProvider implements ValueObject {
    LOCAL,
    LDAP
}
