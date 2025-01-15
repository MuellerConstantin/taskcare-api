package de.mueller_constantin.taskcare.api.core.board.domain;

import de.mueller_constantin.taskcare.api.core.common.domain.ValueObject;

/**
 * These are the roles used by board members. Based on this roles a
 * member can have different permissions within a single board.
 * Hence, The role is only valid within a specific board.
 */
public enum Role implements ValueObject {
    ADMINISTRATOR,
    MAINTAINER,
    MEMBER
}
