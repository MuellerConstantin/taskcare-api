package de.mueller_constantin.taskcare.api.core.user.domain;

import de.mueller_constantin.taskcare.api.core.common.domain.ValueObject;

public enum IdentityProvider implements ValueObject {
    LOCAL,
    LDAP
}
