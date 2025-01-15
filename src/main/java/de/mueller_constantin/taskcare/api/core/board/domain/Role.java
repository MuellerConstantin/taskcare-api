package de.mueller_constantin.taskcare.api.core.board.domain;

import de.mueller_constantin.taskcare.api.core.common.domain.ValueObject;

public enum Role implements ValueObject {
    ADMINISTRATOR,
    MAINTAINER,
    MEMBER
}
