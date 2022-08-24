package de.x1c1b.taskcare.service.infrastructure.persistence.jpa.entity;

import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "members")
@NoArgsConstructor
@Data
public class MemberEntity {

    @EmbeddedId
    private MemberEntityId id;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", insertable = false, updatable = false)
    private BoardEntity board;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username", insertable = false, updatable = false)
    private UserEntity user;

    @Column(name = "role")
    private String role;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "responsible", fetch = FetchType.LAZY)
    private Set<TaskEntity> responsibilities = new HashSet<>();

    public MemberEntity(BoardEntity board, UserEntity user, String role) {
        this.id = new MemberEntityId(board.getId(), user.getUsername());
        this.board = board;
        this.user = user;
        this.role = role;

        board.getMembers().add(this);
        user.getMemberships().add(this);
    }

    @Embeddable
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class MemberEntityId implements Serializable {

        @Column(name = "board_id")
        @Type(type = "uuid-char")
        private UUID boardId;

        @Column(name = "username")
        private String username;
    }

    @PreRemove
    private void clearAssociations() {
        for (TaskEntity taskEntity : responsibilities) {
            taskEntity.setResponsible(null);
        }
    }
}
