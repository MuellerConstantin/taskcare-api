package de.x1c1b.taskcare.service.infrastructure.persistence.jpa.entity.mapper;

import de.x1c1b.taskcare.service.core.board.domain.Board;
import de.x1c1b.taskcare.service.core.board.domain.Member;
import de.x1c1b.taskcare.service.core.board.domain.Task;
import de.x1c1b.taskcare.service.core.common.domain.Page;
import de.x1c1b.taskcare.service.infrastructure.persistence.jpa.entity.BoardEntity;
import de.x1c1b.taskcare.service.infrastructure.persistence.jpa.entity.MemberEntity;
import de.x1c1b.taskcare.service.infrastructure.persistence.jpa.entity.TaskEntity;
import de.x1c1b.taskcare.service.infrastructure.persistence.jpa.entity.UserEntity;
import de.x1c1b.taskcare.service.infrastructure.persistence.jpa.repository.UserEntityRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@Mapper(componentModel = "spring", uses = UserEntityRepository.class,
        injectionStrategy = InjectionStrategy.FIELD,
        builder = @Builder(disableBuilder = true))
public abstract class BoardEntityMapper {

    @Autowired
    private UserEntityRepository userEntityRepository;

    @Mapping(target = "createdAtOffset", source = "createdAt")
    @Mapping(target = "members", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    public abstract BoardEntity mapToEntity(Board boardAggregate);

    @Mapping(target = "createdAt", source = "boardEntity", qualifiedByName = "createdAtFromBoardEntity")
    public abstract Board mapToDomain(BoardEntity boardEntity);

    @Mapping(source = "number", target = "info.page")
    @Mapping(source = "size", target = "info.perPage")
    @Mapping(source = "totalElements", target = "info.totalElements")
    @Mapping(source = "totalPages", target = "info.totalPages")
    @Mapping(source = "content", target = "content", defaultExpression = "java(new ArrayList<>())")
    public abstract Page<Board> mapToDomain(org.springframework.data.domain.Page<BoardEntity> page);

    @Mapping(source = "user.username", target = "username")
    public abstract Member mapToDomain(MemberEntity memberEntity);

    @Mapping(target = "createdAt", source = "taskEntity", qualifiedByName = "createdAtFromTaskEntity")
    @Mapping(target = "expiresAt", source = "taskEntity", qualifiedByName = "expiresAtFromTaskEntity")
    @Mapping(target = "responsible", source = "responsible.id.username")
    public abstract Task mapToDomain(TaskEntity taskEntity);

    @AfterMapping
    void mapToEntityAfterMapping(Board boardAggregate, @MappingTarget BoardEntity boardEntity) {
        boardAggregate.getMembers().forEach(member -> {
            UserEntity userEntity = userEntityRepository.findById(member.getUsername()).orElseThrow();
            MemberEntity memberEntity = new MemberEntity(boardEntity, userEntity, member.getRole().getName());
            boardEntity.getMembers().add(memberEntity);
        });

        boardAggregate.getTasks().forEach(task -> {

            TaskEntity.TaskEntityBuilder taskEntityBuilder = TaskEntity.builder()
                    .id(task.getId())
                    .name(task.getName())
                    .board(boardEntity)
                    .description(task.getDescription())
                    .priority(task.getPriority())
                    .status(task.getStatus().getName())
                    .createdBy(task.getCreatedBy())
                    .createdAt(timestampToInstant(task.getCreatedAt()))
                    .createdAtOffset(timestampToOffset(task.getCreatedAt()))
                    .expiresAt(timestampToInstant(task.getExpiresAt()))
                    .expiresAtOffset(timestampToOffset(task.getExpiresAt()));

            if (null != task.getResponsible()) {
                MemberEntity memberEntity = boardAggregate.getMembers().stream()
                        .filter(member -> member.getUsername().equals(task.getResponsible()))
                        .findFirst()
                        .map(member -> {
                            UserEntity userEntity = userEntityRepository.findById(member.getUsername()).orElseThrow();
                            return new MemberEntity(boardEntity, userEntity, member.getRole().getName());
                        }).orElse(null);

                taskEntityBuilder.responsible(memberEntity);
            } else {
                taskEntityBuilder.responsible(null);
            }

            boardEntity.getTasks().add(taskEntityBuilder.build());
        });
    }

    @Named("createdAtFromBoardEntity")
    OffsetDateTime createdAtFromBoardEntity(BoardEntity boardEntity) {
        if (null != boardEntity.getCreatedAt() && null != boardEntity.getCreatedAtOffset()) {
            return OffsetDateTime.ofInstant(boardEntity.getCreatedAt(), ZoneId.of(boardEntity.getCreatedAtOffset()));
        } else {
            return null;
        }
    }

    @Named("createdAtFromTaskEntity")
    OffsetDateTime createdAtFromTaskEntity(TaskEntity taskEntity) {
        if (null != taskEntity.getCreatedAt() && null != taskEntity.getCreatedAtOffset()) {
            return OffsetDateTime.ofInstant(taskEntity.getCreatedAt(), ZoneId.of(taskEntity.getCreatedAtOffset()));
        } else {
            return null;
        }
    }

    @Named("expiresAtFromTaskEntity")
    OffsetDateTime expiresAtFromTaskEntity(TaskEntity taskEntity) {
        if (null != taskEntity.getExpiresAt() && null != taskEntity.getExpiresAtOffset()) {
            return OffsetDateTime.ofInstant(taskEntity.getExpiresAt(), ZoneId.of(taskEntity.getExpiresAtOffset()));
        } else {
            return null;
        }
    }

    Instant timestampToInstant(OffsetDateTime timestamp) {
        return null == timestamp ? null : timestamp.toInstant();
    }

    String timestampToOffset(OffsetDateTime timestamp) {
        return null == timestamp ? null : timestamp.getOffset().getId();
    }
}
