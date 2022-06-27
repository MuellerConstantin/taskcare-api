package de.x1c1b.taskcare.service.infrastructure.persistence.jpa.entity;

import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
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
    @ManyToOne
    @JoinColumn(name = "board_id", insertable = false, updatable = false)
    private BoardEntity board;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "username", insertable = false, updatable = false)
    private UserEntity user;

    @Column(name = "role")
    private String role;

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
}
