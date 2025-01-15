package de.mueller_constantin.taskcare.api.core.board.domain;

import de.mueller_constantin.taskcare.api.core.common.application.NoSuchEntityException;
import de.mueller_constantin.taskcare.api.core.common.domain.Aggregate;
import de.mueller_constantin.taskcare.api.core.common.domain.DomainEvent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class BoardAggregate extends Aggregate {
    private String name;
    private String description;
    private Set<Member> members = new HashSet<>();
    private Set<Status> statuses = new HashSet<>();

    public BoardAggregate() {
        this(UUID.randomUUID(), 0, false);
    }

    public BoardAggregate(UUID id, int version, boolean deleted) {
        super(id, version, deleted);
    }

    @Override
    protected void processEvent(DomainEvent event) throws IllegalArgumentException {
        if(event instanceof BoardCreatedEvent) {
            this.name = ((BoardCreatedEvent) event).getName();
            this.description = ((BoardCreatedEvent) event).getDescription();
            this.members.add(((BoardCreatedEvent) event).getCreator());
            return;
        } else if(event instanceof BoardUpdatedEvent) {
            this.name = ((BoardUpdatedEvent) event).getName();
            this.description = ((BoardUpdatedEvent) event).getDescription();
            return;
        } else if(event instanceof BoardDeletedEvent) {
            return;
        } else if(event instanceof MemberAddedEvent) {
            this.members.add(((MemberAddedEvent) event).getMember());
            return;
        } else if(event instanceof MemberRemovedEvent) {
            this.members.removeIf(m -> m.getId().equals(((MemberRemovedEvent) event).getMember().getId()));
            return;
        } else if(event instanceof MemberUpdatedEvent) {
            this.members.forEach(member -> {
                if(member.getId().equals(((MemberUpdatedEvent) event).getMemberId())) {
                    member.setRole(((MemberUpdatedEvent) event).getRole());
                }
            });
            return;
        } else if(event instanceof StatusAddedEvent) {
            this.statuses.add(((StatusAddedEvent) event).getStatus());
            return;
        } else if(event instanceof StatusRemovedEvent) {
            this.statuses.removeIf(s -> s.getId().equals(((StatusRemovedEvent) event).getStatus().getId()));
            return;
        } else if(event instanceof StatusUpdatedEvent) {
            this.statuses.forEach(status -> {
                if(status.getId().equals(((StatusUpdatedEvent) event).getStatusId())) {
                    status.setName(((StatusUpdatedEvent) event).getName());
                    status.setDescription(((StatusUpdatedEvent) event).getDescription());
                }
            });
            return;
        }

        throw new IllegalArgumentException("Unknown event type: %s".formatted(event.getClass()));
    }

    @Override
    protected void processDelete() {
        this.applyChange(BoardDeletedEvent.builder()
                .aggregateId(this.getId())
                .version(this.getNextVersion())
                .build());
    }

    public void create(String name, String description, UUID creatorId) {
        this.applyChange(BoardCreatedEvent.builder()
                .aggregateId(this.getId())
                .version(this.getNextVersion())
                .name(name)
                .description(description)
                .creator(new Member(UUID.randomUUID(), this.getId(), creatorId, Role.ADMINISTRATOR))
                .build()
        );
    }

    public void update(String name, String description) {
        this.applyChange(BoardUpdatedEvent.builder()
                .aggregateId(this.getId())
                .version(this.getNextVersion())
                .name(name)
                .description(description)
                .build()
        );
    }

    public void addMember(UUID userId, Role role) {
        boolean alreadyMember = this.members.stream().anyMatch(m -> m.getUserId().equals(userId));

        if(alreadyMember) {
            throw new BoardMemberAlreadyExistsException();
        }

        this.applyChange(MemberAddedEvent.builder()
                .aggregateId(this.getId())
                .version(this.getNextVersion())
                .member(new Member(UUID.randomUUID(), this.getId(), userId, role))
                .build()
        );
    }

    public void removeMember(UUID memberId) {
        Member member = this.members.stream().filter(m -> m.getId().equals(memberId)).findFirst().orElse(null);

        if(member == null) {
            throw new NoSuchEntityException();
        }

        if(member.getRole() == Role.ADMINISTRATOR) {
            boolean onlyAdmin = this.members.stream().noneMatch(m -> m.getRole() == Role.ADMINISTRATOR &&
                    !m.getId().equals(memberId));

            if(onlyAdmin) {
                throw new BoardMustBeAdministrableException("Board must have at least one administrator");
            }
        }

        this.applyChange(MemberRemovedEvent.builder()
                .aggregateId(this.getId())
                .version(this.getNextVersion())
                .member(member)
                .build()
        );
    }

    public void updateMember(UUID memberId, Role role) {
        Member member = this.members.stream().filter(m -> m.getId().equals(memberId)).findFirst().orElse(null);

        if(member == null) {
            throw new NoSuchEntityException();
        }

        if(member.getRole() == Role.ADMINISTRATOR && role != Role.ADMINISTRATOR) {
            boolean onlyAdmin = this.members.stream().noneMatch(m -> m.getRole() == Role.ADMINISTRATOR &&
                    !m.getId().equals(memberId));

            if(onlyAdmin) {
                throw new BoardMustBeAdministrableException("Board must have at least one administrator");
            }
        }

        this.applyChange(MemberUpdatedEvent.builder()
                .aggregateId(this.getId())
                .version(this.getNextVersion())
                .memberId(member.getId())
                .role(role)
                .build()
        );
    }

    public void addStatus(String name, String description) {
        this.applyChange(StatusAddedEvent.builder()
                .aggregateId(this.getId())
                .version(this.getNextVersion())
                .status(new Status(UUID.randomUUID(), this.getId(), name, description))
                .build()
        );
    }

    public void removeStatus(UUID statusId) {
        Status status = this.statuses.stream().filter(s -> s.getId().equals(statusId)).findFirst().orElse(null);

        if(status == null) {
            throw new NoSuchEntityException();
        }

        this.applyChange(StatusRemovedEvent.builder()
                .aggregateId(this.getId())
                .version(this.getNextVersion())
                .status(status)
                .build()
        );
    }

    public void updateStatus(UUID statusId, String name, String description) {
        Status status = this.statuses.stream().filter(s -> s.getId().equals(statusId)).findFirst().orElse(null);

        if(status == null) {
            throw new NoSuchEntityException();
        }

        this.applyChange(StatusUpdatedEvent.builder()
                .aggregateId(this.getId())
                .version(this.getNextVersion())
                .statusId(statusId)
                .name(name)
                .description(description)
                .build()
        );
    }
}
