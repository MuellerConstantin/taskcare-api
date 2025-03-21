package de.mueller_constantin.taskcare.api.core.board.application;

import de.mueller_constantin.taskcare.api.core.board.application.persistence.ComponentReadModelRepository;
import de.mueller_constantin.taskcare.api.core.common.domain.Entity;
import de.mueller_constantin.taskcare.api.core.common.domain.Page;
import de.mueller_constantin.taskcare.api.core.common.domain.PageInfo;
import de.mueller_constantin.taskcare.api.core.board.application.persistence.BoardReadModelRepository;
import de.mueller_constantin.taskcare.api.core.board.application.persistence.MemberReadModelRepository;
import de.mueller_constantin.taskcare.api.core.board.application.persistence.StatusReadModelRepository;
import de.mueller_constantin.taskcare.api.core.board.application.query.*;
import de.mueller_constantin.taskcare.api.core.board.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class BoardReadServiceTest {
    @Mock
    private BoardReadModelRepository boardReadModelRepository;

    @Mock
    private MemberReadModelRepository memberReadModelRepository;

    @Mock
    private StatusReadModelRepository statusReadModelRepository;

    @Mock
    private ComponentReadModelRepository componentReadModelRepository;

    @InjectMocks
    private BoardReadService boardReadService;

    private UUID id;
    private UUID creatorUserId;
    private UUID creatorMemberId;
    private UUID dummyUserId;
    private UUID dummyMemberId;
    private BoardAggregate boardAggregate;
    private BoardProjection boardProjection;
    private List<MemberProjection> memberProjections;
    private List<StatusProjection> statusProjections;
    private List<ComponentProjection> componentProjections;

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
                .build();

        this.memberProjections = List.of(
                MemberProjection.builder()
                        .id(this.creatorMemberId)
                        .userId(this.creatorUserId)
                        .role(Role.ADMINISTRATOR)
                        .build(),
                MemberProjection.builder()
                        .id(this.dummyMemberId)
                        .userId(this.dummyUserId)
                        .role(Role.MEMBER)
                        .build()
        );

        this.statusProjections = List.of(
                StatusProjection.builder()
                        .id(UUID.randomUUID())
                        .name("To Do")
                        .description("Tasks that need to be done")
                        .build(),
                StatusProjection.builder()
                        .id(UUID.randomUUID())
                        .name("In Progress")
                        .description("Tasks that are currently in progress")
                        .build(),
                StatusProjection.builder()
                        .id(UUID.randomUUID())
                        .name("Done")
                        .description("Tasks that have been completed")
                        .build()
        );

        this.componentProjections = List.of(
                ComponentProjection.builder()
                        .id(UUID.randomUUID())
                        .name("Component 1")
                        .description("Description 1")
                        .build(),
                ComponentProjection.builder()
                        .id(UUID.randomUUID())
                        .name("Component 2")
                        .description("Description 2")
                        .build(),
                ComponentProjection.builder()
                        .id(UUID.randomUUID())
                        .name("Component 3")
                        .description("Description 3")
                        .build()
        );
    }

    @Test
    void handleFindUserByIdQuery() {
        when(boardReadModelRepository.findById(id)).thenReturn(Optional.of(boardProjection));

        BoardProjection result = boardReadService.query(FindBoardByIdQuery.builder()
                .id(this.id)
                .build());

        assertEquals(boardProjection, result);
    }

    @Test
    void handleFindAllBoardsQuery() {
        when(boardReadModelRepository.findAll(any(PageInfo.class), eq(null))).thenReturn(Page.<BoardProjection>builder()
                .content(List.of(boardProjection))
                .info(PageInfo.builder()
                        .page(0)
                        .perPage(10)
                        .build())
                .build());

        Page<BoardProjection> result = boardReadService.query(FindAllBoardsQuery.builder()
                .page(0)
                .perPage(10)
                .build());

        assertEquals(boardProjection, result.getContent().get(0));
        verify(boardReadModelRepository, times(1)).findAll(any(PageInfo.class), eq(null));
    }

    @Test
    void handleFindAllBoardsUserIsMemberQuery() {
        when(boardReadModelRepository.findAllUserIsMember(eq(this.creatorUserId), any(PageInfo.class), eq(null))).thenReturn(Page.<BoardProjection>builder()
                .content(List.of(boardProjection))
                .info(PageInfo.builder()
                        .page(0)
                        .perPage(10)
                        .build())
                .build());

        Page<BoardProjection> result = boardReadService.query(FindAllBoardsUserIsMemberQuery.builder()
                .userId(this.creatorUserId)
                .page(0)
                .perPage(10)
                .build());

        assertEquals(boardProjection, result.getContent().get(0));
        verify(boardReadModelRepository, times(1)).findAllUserIsMember(eq(this.creatorUserId), any(PageInfo.class), eq(null));
    }

    @Test
    void handleFindAllMembersByBoardIdQuery() {
        when(memberReadModelRepository.findAllByBoardId(eq(this.id), any(PageInfo.class), any())).thenReturn(Page.<MemberProjection>builder()
                .content(memberProjections)
                .info(PageInfo.builder()
                        .page(0)
                        .perPage(10)
                        .build())
                .build());

        Page<MemberProjection> result = boardReadService.query(FindAllMembersByBoardIdQuery.builder()
                .boardId(this.id)
                .page(0)
                .perPage(10)
                .build());

        assertEquals(memberProjections, result.getContent());
    }

    @Test
    void handleFindAllStatusesByBoardIdQuery() {
        when(statusReadModelRepository.findAllByBoardId(eq(this.id), any(PageInfo.class), any())).thenReturn(Page.<StatusProjection>builder()
                .content(statusProjections)
                .info(PageInfo.builder()
                        .page(0)
                        .perPage(10)
                        .build())
                .build());

        Page<StatusProjection> result = boardReadService.query(FindAllStatusesByBoardIdQuery.builder()
                .boardId(this.id)
                .page(0)
                .perPage(10)
                .build());

        assertEquals(statusProjections, result.getContent());
    }

    @Test
    void handleFindAllComponentsByBoardIdQuery() {
        when(componentReadModelRepository.findAllByBoardId(eq(this.id), any(PageInfo.class), any())).thenReturn(Page.<ComponentProjection>builder()
                .content(componentProjections)
                .info(PageInfo.builder()
                        .page(0)
                        .perPage(10)
                        .build())
                .build());

        Page<ComponentProjection> result = boardReadService.query(FindAllComponentsByBoardIdQuery.builder()
                .boardId(this.id)
                .page(0)
                .perPage(10)
                .build());

        assertEquals(componentProjections, result.getContent());
    }
}
