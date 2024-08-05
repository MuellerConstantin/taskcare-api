package de.x1c1b.taskcare.api.infrastructure.persistence.jpa.entity;

import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "boards")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BoardEntity {

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

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "board", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<MemberEntity> members = new HashSet<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "board", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<TaskEntity> tasks = new ArrayList<>();
}
