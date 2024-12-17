package de.mueller_constantin.taskcare.api.core.kanban.domain;

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

    public BoardAggregate() {
        this(UUID.randomUUID(), 0, false);
    }

    public BoardAggregate(UUID id, int version, boolean deleted) {
        super(id, version, deleted);
    }

    @Override
    protected void processEvent(DomainEvent event) throws IllegalArgumentException {
        if(event instanceof BoardCreatedEvent) {
            this.setName(((BoardCreatedEvent) event).getName());
            this.setDescription(((BoardCreatedEvent) event).getDescription());
            this.addMember(((BoardCreatedEvent) event).getCreator());
            return;
        } else if(event instanceof BoardUpdatedEvent) {
            this.setName(((BoardUpdatedEvent) event).getName());
            this.setDescription(((BoardUpdatedEvent) event).getDescription());
            return;
        } else if(event instanceof BoardDeletedEvent) {
            return;
        } else if(event instanceof MemberAddedEvent) {
            this.addMember(((MemberAddedEvent) event).getMember());
            return;
        } else if(event instanceof MemberRemovedEvent) {
            this.members.remove(((MemberRemovedEvent) event).getMember());
            return;
        } else if(event instanceof MemberUpdatedEvent) {
            this.members.forEach(member -> {
                if(member.getId().equals(((MemberUpdatedEvent) event).getMemberId())) {
                    member.setRole(((MemberUpdatedEvent) event).getRole());
                }
            });
            return;
        }

        throw new IllegalArgumentException("Unknown event type: %s".formatted(event.getClass()));
    }

    private void setName(String name) {
        this.name = name;
    }

    private void setDescription(String description) {
        this.description = description;
    }

    private void addMember(Member member) {
        this.members.add(member);
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
                .creator(new Member(UUID.randomUUID(), creatorId, Role.ADMINISTRATOR))
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
                .member(new Member(UUID.randomUUID(), userId, role))
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
}
