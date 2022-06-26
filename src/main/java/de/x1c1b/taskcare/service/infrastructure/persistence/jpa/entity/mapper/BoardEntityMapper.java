package de.x1c1b.taskcare.service.infrastructure.persistence.jpa.entity.mapper;

import de.x1c1b.taskcare.service.core.board.domain.Board;
import de.x1c1b.taskcare.service.core.board.domain.Member;
import de.x1c1b.taskcare.service.core.board.domain.Role;
import de.x1c1b.taskcare.service.core.common.domain.Page;
import de.x1c1b.taskcare.service.infrastructure.persistence.jpa.entity.BoardEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface BoardEntityMapper {

    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "timestampToInstant")
    @Mapping(target = "createdAtOffset", source = "createdAt", qualifiedByName = "timestampToOffset")
    @Mapping(target = "members", ignore = true)
    BoardEntity mapToEntity(Board boardAggregate);

    @Mapping(target = "createdAt", source = "boardEntity", qualifiedByName = "timestampFromBoardEntity")
    @Mapping(target = "members", source = "boardEntity", qualifiedByName = "membersFromBoardEntity")
    Board mapToDomain(BoardEntity boardEntity);

    @Mapping(source = "number", target = "page")
    @Mapping(source = "size", target = "perPage")
    @Mapping(source = "content", target = "content", defaultExpression = "java(new ArrayList<>())")
    Page<Board> mapToDomain(org.springframework.data.domain.Page<BoardEntity> page);

    @Named("membersFromBoardEntity")
    default Set<Member> membersFromBoardEntity(BoardEntity boardEntity) {
        return boardEntity.getMembers().stream()
                .map(member -> Member.builder()
                        .username(member.getUser().getUsername())
                        .role(Role.valueOf(member.getRole()))
                        .build())
                .collect(Collectors.toSet());
    }

    @Named("timestampToInstant")
    default Instant timestampToInstant(OffsetDateTime timestamp) {
        return timestamp.toInstant();
    }

    @Named("timestampToOffset")
    default String timestampToOffset(OffsetDateTime timestamp) {
        return timestamp.getOffset().getId();
    }

    @Named("timestampFromBoardEntity")
    default OffsetDateTime timestampFromBoardEntity(BoardEntity entity) {
        return OffsetDateTime.ofInstant(entity.getCreatedAt(), ZoneId.of(entity.getCreatedAtOffset()));
    }
}
