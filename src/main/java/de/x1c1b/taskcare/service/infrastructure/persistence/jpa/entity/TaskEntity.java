package de.x1c1b.taskcare.service.infrastructure.persistence.jpa.entity;

import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tasks")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TaskEntity {

    @Id
    @Type(type = "uuid-char")
    @Column(name = "id", unique = true, nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", length = 2000)
    private String description;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "created_at_offset")
    private String createdAtOffset;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "priority")
    private Integer priority;

    @Column(name = "status")
    private String status;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "expires_at_offset")
    private String expiresAtOffset;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private BoardEntity board;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsible_board_id", referencedColumnName = "board_id")
    @JoinColumn(name = "responsible_username", referencedColumnName = "username")
    private MemberEntity responsible;
}
