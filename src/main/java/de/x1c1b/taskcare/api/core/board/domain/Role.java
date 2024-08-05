package de.x1c1b.taskcare.api.core.board.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Role {

    ADMINISTRATOR("ADMINISTRATOR"),
    MAINTAINER("MAINTAINER"),
    USER("USER"),
    VISITOR("VISITOR");

    private final String name;
}
