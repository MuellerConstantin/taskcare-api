package de.x1c1b.taskcare.api.core.user.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class User {

    private String username;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private boolean enabled;
    private boolean locked;
}
