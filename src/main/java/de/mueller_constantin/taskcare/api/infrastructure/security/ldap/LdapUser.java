package de.mueller_constantin.taskcare.api.infrastructure.security.ldap;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class LdapUser {
    private String username;
    private String displayName;
}
