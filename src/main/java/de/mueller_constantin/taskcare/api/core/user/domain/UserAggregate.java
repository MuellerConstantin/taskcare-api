package de.mueller_constantin.taskcare.api.core.user.domain;

import de.mueller_constantin.taskcare.api.core.common.domain.Aggregate;
import de.mueller_constantin.taskcare.api.core.common.domain.DomainEvent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

/**
 * Represents a user account. A user account is required to access
 * the application. It is used for authentication and system-wide
 * authorization.
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class UserAggregate extends Aggregate {
    public static final String DEFAULT_ADMIN_USERNAME = "tc_admin";

    private String username;
    private String password;
    private String displayName;
    private Role role;
    private IdentityProvider identityProvider;
    private boolean locked;

    public UserAggregate() {
        this(UUID.randomUUID(), 0, false);
    }

    public UserAggregate(UUID id, int version, boolean deleted) {
        super(id, version, deleted);
    }

    @Override
    protected void processEvent(DomainEvent event) throws IllegalArgumentException {
        if(event instanceof UserCreatedEvent) {
            this.username = ((UserCreatedEvent) event).getUsername();
            this.password = ((UserCreatedEvent) event).getPassword();
            this.displayName = ((UserCreatedEvent) event).getDisplayName();
            this.role = ((UserCreatedEvent) event).getRole();
            this.identityProvider = ((UserCreatedEvent) event).getIdentityProvider();
            this.locked = false;
            return;
        } else if(event instanceof UserUpdatedEvent) {
            this.password = ((UserUpdatedEvent) event).getPassword();
            this.displayName = ((UserUpdatedEvent) event).getDisplayName();
            this.role = ((UserUpdatedEvent) event).getRole();
            return;
        } else if(event instanceof UserLockedEvent) {
            this.locked = true;
            return;
        } else if(event instanceof UserUnlockedEvent) {
            this.locked = false;
            return;
        } else if(event instanceof UserDeletedEvent) {
            return;
        }

        throw new IllegalArgumentException("Unknown event type: %s".formatted(event.getClass()));
    }

    @Override
    protected void processDelete() {
        this.applyChange(UserDeletedEvent.builder()
                .aggregateId(this.getId())
                .version(this.getNextVersion())
                .build());
    }

    public void create(String username, String password, String displayName, Role role,
                       IdentityProvider identityProvider) {
        this.applyChange(UserCreatedEvent.builder()
                .aggregateId(this.getId())
                .version(this.getNextVersion())
                .username(username)
                .password(password)
                .displayName(displayName)
                .role(role)
                .identityProvider(identityProvider)
                .build()
        );
    }

    public void update(String password, String displayName, Role role) {
        this.applyChange(UserUpdatedEvent.builder()
                .aggregateId(this.getId())
                .version(this.getNextVersion())
                .password(password)
                .displayName(displayName)
                .role(role)
                .build()
        );
    }

    public void lock() {
        this.applyChange(UserLockedEvent.builder()
                .aggregateId(this.getId())
                .version(this.getNextVersion())
                .build()
        );
    }

    public void unlock() {
        this.applyChange(UserUnlockedEvent.builder()
                .aggregateId(this.getId())
                .version(this.getNextVersion())
                .build()
        );
    }
}
