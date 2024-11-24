package de.mueller_constantin.taskcare.api.core.user.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Role {
    ADMINISTRATOR("Administrator"),
    USER("User");

    private final String name;
}
