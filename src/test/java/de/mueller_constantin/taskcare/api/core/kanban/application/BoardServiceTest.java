package de.mueller_constantin.taskcare.api.core.kanban.application;

import de.mueller_constantin.taskcare.api.core.common.application.NoSuchEntityException;
import de.mueller_constantin.taskcare.api.core.common.domain.Entity;
import de.mueller_constantin.taskcare.api.core.common.domain.Page;
import de.mueller_constantin.taskcare.api.core.common.domain.PageInfo;
import de.mueller_constantin.taskcare.api.core.kanban.application.persistence.BoardEventStoreRepository;
import de.mueller_constantin.taskcare.api.core.kanban.application.persistence.BoardReadModelRepository;
import de.mueller_constantin.taskcare.api.core.kanban.domain.*;
import de.mueller_constantin.taskcare.api.core.user.application.ExistsUserByIdQuery;
import de.mueller_constantin.taskcare.api.core.user.application.UserService;
import jakarta.validation.Validator;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BoardServiceTest {
    @Mock
    private BoardEventStoreRepository boardEventStoreRepository;

    @Mock
    private BoardReadModelRepository boardReadModelRepository;

    @Mock
    private UserService userService;

    @Mock
    private Validator validator;

    @InjectMocks
    private BoardService boardService;

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
    void handleCreateBoardCommandWithUnknownCreator() {
        when(userService.query(any(ExistsUserByIdQuery.class))).thenReturn(false);

        assertThrows(NoSuchEntityException.class, () -> {
            boardService.dispatch(CreateBoardCommand.builder()
                    .name("Second Test Board")
                    .description("Lorem ipsum dolor sit amet, consetetur sadipscing elitr")
                    .creatorId(UUID.randomUUID())
                    .build());
        });
    }

    @Test
    void handleCreateBoardCommand() {
        when(userService.query(any(ExistsUserByIdQuery.class))).thenReturn(true);
        doNothing().when(boardEventStoreRepository).save(any(BoardAggregate.class));

        boardService.dispatch(CreateBoardCommand.builder()
                .name("Second Test Board")
                .description("Lorem ipsum dolor sit amet, consetetur sadipscing elitr")
                .creatorId(UUID.randomUUID())
                .build());

        verify(userService, times(1)).query(any(ExistsUserByIdQuery.class));
        verify(boardEventStoreRepository, times(1)).save(any(BoardAggregate.class));
    }

    @Test
    void handleUpdateBoardByIdCommand() {
        when(boardEventStoreRepository.load(id)).thenReturn(Optional.of(boardAggregate));
        doNothing().when(boardEventStoreRepository).save(any(BoardAggregate.class));

        boardService.dispatch(UpdateBoardByIdCommand.builder()
                .id(id)
                .name("Another Test Board")
                .build());

        verify(boardEventStoreRepository, times(1)).load(id);
        verify(boardEventStoreRepository, times(1)).save(any(BoardAggregate.class));
    }

    @Test
    void handleUpdateBoardByIdCommandUnknownId() {
        when(boardEventStoreRepository.load(id)).thenReturn(Optional.empty());

        assertThrows(NoSuchEntityException.class, () -> {
            boardService.dispatch(UpdateBoardByIdCommand.builder()
                    .id(id)
                    .name("Another Test Board")
                    .build());
        });
    }

    @Test
    void handleDeleteBoardByIdCommand() {
        when(boardEventStoreRepository.load(id)).thenReturn(Optional.of(boardAggregate));
        doNothing().when(boardEventStoreRepository).save(any(BoardAggregate.class));

        boardService.dispatch(DeleteBoardByIdCommand.builder()
                .id(id)
                .build());

        verify(boardEventStoreRepository, times(1)).load(id);
        verify(boardEventStoreRepository, times(1)).save(any(BoardAggregate.class));
    }

    @Test
    void handleDeleteBoardByIdCommandUnknownId() {
        when(boardEventStoreRepository.load(id)).thenReturn(Optional.empty());

        assertThrows(NoSuchEntityException.class, () -> {
            boardService.dispatch(DeleteBoardByIdCommand.builder()
                    .id(id)
                    .build());
        });
    }

    @Test
    void handleAddMemberByIdCommand() {
        when(userService.query(any(ExistsUserByIdQuery.class))).thenReturn(true);
        when(boardEventStoreRepository.load(id)).thenReturn(Optional.of(boardAggregate));
        doNothing().when(boardEventStoreRepository).save(any(BoardAggregate.class));

        boardService.dispatch(AddMemberByIdCommand.builder()
                .boardId(id)
                .userId(UUID.randomUUID())
                .role(Role.MEMBER)
                .build());

        assertEquals(3, boardAggregate.getMembers().size());

        verify(userService, times(1)).query(any(ExistsUserByIdQuery.class));
        verify(boardEventStoreRepository, times(1)).save(any(BoardAggregate.class));
    }

    @Test
    void handleAddMemberByIdCommandWithUnknownUser() {
        when(userService.query(any(ExistsUserByIdQuery.class))).thenReturn(false);

        assertThrows(NoSuchEntityException.class, () -> {
            boardService.dispatch(AddMemberByIdCommand.builder()
                    .boardId(id)
                    .userId(UUID.randomUUID())
                    .role(Role.MEMBER)
                    .build());
        });
    }

    @Test
    void handleAddMemberByIdCommandWithAlreadyExistingMember() {
        when(userService.query(any(ExistsUserByIdQuery.class))).thenReturn(true);
        when(boardEventStoreRepository.load(id)).thenReturn(Optional.of(boardAggregate));

        assertThrows(BoardMemberAlreadyExistsException.class, () -> {
            boardService.dispatch(AddMemberByIdCommand.builder()
                    .boardId(id)
                    .userId(creatorUserId)
                    .role(Role.MEMBER)
                    .build());
        });
    }

    @Test
    void handleRemoveMemberByIdCommand() {
        when(boardEventStoreRepository.load(id)).thenReturn(Optional.of(boardAggregate));
        doNothing().when(boardEventStoreRepository).save(any(BoardAggregate.class));

        boardService.dispatch(RemoveMemberByIdCommand.builder()
                .boardId(id)
                .memberId(dummyMemberId)
                .build());

        assertEquals(1, boardAggregate.getMembers().size());

        verify(boardEventStoreRepository, times(1)).save(any(BoardAggregate.class));
    }

    @Test
    void handleRemoveMemberByIdCommandWithMissingMember() {
        when(boardEventStoreRepository.load(id)).thenReturn(Optional.of(boardAggregate));

        assertThrows(NoSuchEntityException.class, () -> {
            boardService.dispatch(RemoveMemberByIdCommand.builder()
                    .boardId(id)
                    .memberId(UUID.randomUUID())
                    .build());
        });
    }

    @Test
    void handleRemoveMemberByIdCommandRemoveLastAdmin() {
        when(boardEventStoreRepository.load(id)).thenReturn(Optional.of(boardAggregate));

        assertThrows(BoardMustBeAdministrableException.class, () -> {
            boardService.dispatch(RemoveMemberByIdCommand.builder()
                    .boardId(id)
                    .memberId(creatorMemberId)
                    .build());
        });
    }

    @Test
    void handleUpdateMemberByIdCommand() {
        when(boardEventStoreRepository.load(id)).thenReturn(Optional.of(boardAggregate));
        doNothing().when(boardEventStoreRepository).save(any(BoardAggregate.class));

        boardService.dispatch(UpdateMemberByIdCommand.builder()
                .boardId(id)
                .memberId(dummyMemberId)
                .role(Role.MAINTAINER)
                .build());

        verify(boardEventStoreRepository, times(1)).save(any(BoardAggregate.class));
    }

    @Test
    void handleUpdateMemberByIdCommandWithMissingMember() {
        when(boardEventStoreRepository.load(id)).thenReturn(Optional.of(boardAggregate));

        assertThrows(NoSuchEntityException.class, () -> {
            boardService.dispatch(UpdateMemberByIdCommand.builder()
                    .boardId(id)
                    .memberId(UUID.randomUUID())
                    .role(Role.MAINTAINER)
                    .build());
        });
    }

    @Test
    void handleUpdateMemberByIdCommandChangeLastAdminRole() {
        when(boardEventStoreRepository.load(id)).thenReturn(Optional.of(boardAggregate));

        assertThrows(BoardMustBeAdministrableException.class, () -> {
            boardService.dispatch(UpdateMemberByIdCommand.builder()
                    .boardId(id)
                    .memberId(creatorMemberId)
                    .role(Role.MAINTAINER)
                    .build());
        });
    }

    @Test
    void handleFindUserByIdQuery() {
        when(boardReadModelRepository.findById(id)).thenReturn(Optional.of(boardProjection));

        BoardProjection result = boardService.query(FindBoardByIdQuery.builder()
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

        Page<BoardProjection> result = boardService.query(FindAllBoardsQuery.builder()
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

        Page<BoardProjection> result = boardService.query(FindAllBoardsUserIsMemberQuery.builder()
                .userId(this.creatorUserId)
                .page(0)
                .perPage(10)
                .build());

        assertEquals(boardProjection, result.getContent().get(0));
        verify(boardReadModelRepository, times(1)).findAllUserIsMember(eq(this.creatorUserId), any(PageInfo.class));
    }
}
