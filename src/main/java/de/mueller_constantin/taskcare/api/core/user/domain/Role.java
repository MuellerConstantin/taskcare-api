package de.mueller_constantin.taskcare.api.core.user.domain;

import de.mueller_constantin.taskcare.api.core.common.domain.ValueObject;

/**
 * These are the roles used by users and apply to the whole system. They are
 * primarily used for authorization of system/application changes/actions.
 */
public enum Role implements ValueObject {
    ADMINISTRATOR,
    USER
}
