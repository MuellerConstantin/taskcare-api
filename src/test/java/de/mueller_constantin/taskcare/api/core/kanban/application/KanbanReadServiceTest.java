package de.mueller_constantin.taskcare.api.core.kanban.application;

import de.mueller_constantin.taskcare.api.core.common.domain.Entity;
import de.mueller_constantin.taskcare.api.core.common.domain.Page;
import de.mueller_constantin.taskcare.api.core.common.domain.PageInfo;
import de.mueller_constantin.taskcare.api.core.kanban.application.persistence.BoardReadModelRepository;
import de.mueller_constantin.taskcare.api.core.kanban.application.query.FindAllBoardsQuery;
import de.mueller_constantin.taskcare.api.core.kanban.application.query.FindAllBoardsUserIsMemberQuery;
import de.mueller_constantin.taskcare.api.core.kanban.application.query.FindBoardByIdQuery;
import de.mueller_constantin.taskcare.api.core.kanban.domain.BoardAggregate;
import de.mueller_constantin.taskcare.api.core.kanban.domain.BoardProjection;
import de.mueller_constantin.taskcare.api.core.kanban.domain.MemberProjection;
import de.mueller_constantin.taskcare.api.core.kanban.domain.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class KanbanReadServiceTest {
    @Mock
    private BoardReadModelRepository boardReadModelRepository;

    @InjectMocks
    private KanbanReadService kanbanReadService;

    private UUID id;
    private UUID creatorUserId;
    private UUID creatorMemberId;
    private UUID dummyUserId;
    private UUID dummyMemberId;
    private BoardAggregate boardAggregate;
    private BoardProjection boardProjection;

    @BeforeEach
    void setUp() {
        this.id = UUID.randomUUID();
        this.creatorUserId = UUID.randomUUID();
        this.dummyUserId = UUID.randomUUID();

        this.boardAggregate = new BoardAggregate(this.id, 0, false);
        this.boardAggregate.create("Test Board", "Lorem ipsum dolor sit amet, consetetur sadipscing elitr", this.creatorUserId);
        this.boardAggregate.addMember(this.dummyUserId, Role.MEMBER);
        this.boardAggregate.commit();

        this.creatorMemberId = this.boardAggregate.getMembers().stream()
                .filter(m -> m.getUserId().equals(creatorUserId))
                .findFirst()
                .map(Entity::getId)
                .orElse(null);

        this.dummyMemberId = this.boardAggregate.getMembers().stream()
                .filter(m -> m.getUserId().equals(dummyUserId))
                .findFirst()
                .map(Entity::getId)
                .orElse(null);

        this.boardProjection = BoardProjection.builder()
                .id(this.id)
                .name("Test Board")
                .description("Lorem ipsum dolor sit amet, consetetur sadipscing elitr")
                .members(Set.of(
                        MemberProjection.builder()
                                .id(this.creatorMemberId)
                                .userId(this.creatorUserId)
                                .role(Role.ADMINISTRATOR)
                                .build(),
                        MemberProjection.builder()
                                .id(this.dummyMemberId)
                                .userId(this.dummyUserId)
                                .role(Role.MEMBER)
                                .build()))
                .build();
    }

    @Test
    void handleFindUserByIdQuery() {
        when(boardReadModelRepository.findById(id)).thenReturn(Optional.of(boardProjection));

        BoardProjection result = kanbanReadService.query(FindBoardByIdQuery.builder()
                .id(this.id)
                .build());

        assertEquals(boardProjection, result);
    }

    @Test
    void handleFindAllBoardsQuery() {
        when(boardReadModelRepository.findAll(any(PageInfo.class))).thenReturn(Page.<BoardProjection>builder()
                .content(List.of(boardProjection))
                .info(PageInfo.builder()
                        .page(0)
                        .perPage(10)
                        .build())
                .build());

        Page<BoardProjection> result = kanbanReadService.query(FindAllBoardsQuery.builder()
                .page(0)
                .perPage(10)
                .build());

        assertEquals(boardProjection, result.getContent().get(0));
        verify(boardReadModelRepository, times(1)).findAll(any(PageInfo.class));
    }

    @Test
    void handleFindAllBoardsUserIsMemberQuery() {
        when(boardReadModelRepository.findAllUserIsMember(eq(this.creatorUserId), any(PageInfo.class))).thenReturn(Page.<BoardProjection>builder()
                .content(List.of(boardProjection))
                .info(PageInfo.builder()
                        .page(0)
                        .perPage(10)
                        .build())
                .build());

        Page<BoardProjection> result = kanbanReadService.query(FindAllBoardsUserIsMemberQuery.builder()
                .userId(this.creatorUserId)
                .page(0)
                .perPage(10)
                .build());

        assertEquals(boardProjection, result.getContent().get(0));
        verify(boardReadModelRepository, times(1)).findAllUserIsMember(eq(this.creatorUserId), any(PageInfo.class));
    }
}