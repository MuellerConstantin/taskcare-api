package de.mueller_constantin.taskcare.api.core.user.domain.model;

import de.mueller_constantin.taskcare.api.core.common.domain.model.Aggregate;
import de.mueller_constantin.taskcare.api.core.common.domain.model.Event;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class UserAggregate extends Aggregate {
    private String username;
    private String password;
    private String displayName;
    private Role role;
    private boolean locked;

    public UserAggregate() {
        this(UUID.randomUUID(), 0, false);
    }

    public UserAggregate(UUID id, int version, boolean deleted) {
        super(id, version, deleted);
    }

    @Override
    protected void processEvent(Event event) throws IllegalArgumentException {
        if(event instanceof UserCreatedEvent) {
            this.setUsername(((UserCreatedEvent) event).getUsername());
            this.setPassword(((UserCreatedEvent) event).getPassword());
            this.setDisplayName(((UserCreatedEvent) event).getDisplayName());
            this.setRole(((UserCreatedEvent) event).getRole());
            this.setLocked(false);
            return;
        } else if(event instanceof UserUpdatedEvent) {
            this.setPassword(((UserUpdatedEvent) event).getPassword());
            this.setDisplayName(((UserUpdatedEvent) event).getDisplayName());
            this.setRole(((UserUpdatedEvent) event).getRole());
            return;
        } else if(event instanceof UserLockedEvent) {
            this.setLocked(true);
        } else if(event instanceof UserUnlockedEvent) {
            this.setLocked(false);
        } else if(event instanceof UserDeletedEvent) {
            return;
        }

        throw new IllegalArgumentException("Unknown event type");
    }

    private void setUsername(String username) {
        this.username = username;
    }

    private void setPassword(String password) {
        this.password = password;
    }

    private void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    private void setRole(Role role) {
        this.role = role;
    }

    private void setLocked(boolean locked) {
        this.locked = locked;
    }

    @Override
    protected void processDelete() {
        this.applyChange(UserDeletedEvent.builder()
                .aggregateId(this.getId())
                .version(this.getNextVersion())
                .build());
    }

    public void create(String username, String password, String displayName, Role role) {
        this.applyChange(UserCreatedEvent.builder()
                .aggregateId(this.getId())
                .version(this.getNextVersion())
                .username(username)
                .password(password)
                .displayName(displayName)
                .role(role)
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
