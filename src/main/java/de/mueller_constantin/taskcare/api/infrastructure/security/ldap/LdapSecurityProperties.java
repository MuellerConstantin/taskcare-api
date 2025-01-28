package de.mueller_constantin.taskcare.api.infrastructure.security.ldap;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "taskcare.security.ldap")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class LdapSecurityProperties {
    private String url;
    private String base;
    private String managerDn;
    private String managerPassword;
    private String userSearchBase;
    private String userSearchFilter;
    private LdapSyncProperties sync;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class LdapSyncProperties {
        private String usernameField;
        private String displayNameField;
        private String photoField;
    }
}
