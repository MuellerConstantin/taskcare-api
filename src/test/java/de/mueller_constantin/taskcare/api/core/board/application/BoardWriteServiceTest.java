package de.mueller_constantin.taskcare.api.core.board.application;

import de.mueller_constantin.taskcare.api.core.common.application.NoSuchEntityException;
import de.mueller_constantin.taskcare.api.core.common.application.event.DomainEventBus;
import de.mueller_constantin.taskcare.api.core.common.domain.Entity;
import de.mueller_constantin.taskcare.api.core.board.application.command.*;
import de.mueller_constantin.taskcare.api.core.board.application.persistence.BoardEventStoreRepository;
import de.mueller_constantin.taskcare.api.core.board.application.persistence.BoardReadModelRepository;
import de.mueller_constantin.taskcare.api.core.board.domain.*;
import de.mueller_constantin.taskcare.api.core.user.application.UserReadService;
import de.mueller_constantin.taskcare.api.core.user.application.query.ExistsUserByIdQuery;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.integration.util.CheckedRunnable;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BoardWriteServiceTest {
    @Mock
    private BoardEventStoreRepository boardEventStoreRepository;

    @Mock
    private BoardReadModelRepository boardReadModelRepository;

    @Mock
    private UserReadService userReadService;

    @Mock
    private DomainEventBus domainEventBus;

    @Mock
    LockRegistry lockRegistry;

    @InjectMocks
    private BoardWriteService boardWriteService;

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
                .build();
    }

    @Test
    void handleCreateBoardCommandWithUnknownCreator() {
        when(userReadService.query(any(ExistsUserByIdQuery.class))).thenReturn(false);

        assertThrows(NoSuchEntityException.class, () -> {
            boardWriteService.dispatch(CreateBoardCommand.builder()
                    .name("Second Test Board")
                    .description("Lorem ipsum dolor sit amet, consetetur sadipscing elitr")
                    .creatorId(UUID.randomUUID())
                    .build());
        });
    }

    @Test
    void handleCreateBoardCommand() {
        when(userReadService.query(any(ExistsUserByIdQuery.class))).thenReturn(true);
        doNothing().when(boardEventStoreRepository).save(any(BoardAggregate.class));

        boardWriteService.dispatch(CreateBoardCommand.builder()
                .name("Second Test Board")
                .description("Lorem ipsum dolor sit amet, consetetur sadipscing elitr")
                .creatorId(UUID.randomUUID())
                .build());

        verify(userReadService, times(1)).query(any(ExistsUserByIdQuery.class));
        verify(boardEventStoreRepository, times(1)).save(any(BoardAggregate.class));
    }

    @Test
    @SneakyThrows
    void handleUpdateBoardByIdCommand() {
        when(boardEventStoreRepository.load(id)).thenReturn(Optional.of(boardAggregate));
        doNothing().when(boardEventStoreRepository).save(any(BoardAggregate.class));

        doAnswer(invocation -> {
            CheckedRunnable<?> runnable = invocation.getArgument(1);
            runnable.run();
            return null;
        }).when(lockRegistry).executeLocked(any(), any(CheckedRunnable.class));

        boardWriteService.dispatch(UpdateBoardByIdCommand.builder()
                .id(id)
                .name("Another Test Board")
                .build());

        verify(boardEventStoreRepository, times(1)).load(id);
        verify(boardEventStoreRepository, times(1)).save(any(BoardAggregate.class));
    }

    @Test
    @SneakyThrows
    void handleUpdateBoardByIdCommandUnknownId() {
        when(boardEventStoreRepository.load(id)).thenReturn(Optional.empty());

        doAnswer(invocation -> {
            CheckedRunnable<?> runnable = invocation.getArgument(1);
            runnable.run();
            return null;
        }).when(lockRegistry).executeLocked(any(), any(CheckedRunnable.class));

        assertThrows(NoSuchEntityException.class, () -> {
            boardWriteService.dispatch(UpdateBoardByIdCommand.builder()
                    .id(id)
                    .name("Another Test Board")
                    .build());
        });
    }

    @Test
    @SneakyThrows
    void handleDeleteBoardByIdCommand() {
        when(boardEventStoreRepository.load(id)).thenReturn(Optional.of(boardAggregate));
        doNothing().when(boardEventStoreRepository).save(any(BoardAggregate.class));

        doAnswer(invocation -> {
            CheckedRunnable<?> runnable = invocation.getArgument(1);
            runnable.run();
            return null;
        }).when(lockRegistry).executeLocked(any(), any(CheckedRunnable.class));

        boardWriteService.dispatch(DeleteBoardByIdCommand.builder()
                .id(id)
                .build());

        verify(boardEventStoreRepository, times(1)).load(id);
        verify(boardEventStoreRepository, times(1)).save(any(BoardAggregate.class));
    }

    @Test
    @SneakyThrows
    void handleDeleteBoardByIdCommandUnknownId() {
        when(boardEventStoreRepository.load(id)).thenReturn(Optional.empty());

        doAnswer(invocation -> {
            CheckedRunnable<?> runnable = invocation.getArgument(1);
            runnable.run();
            return null;
        }).when(lockRegistry).executeLocked(any(), any(CheckedRunnable.class));

        assertThrows(NoSuchEntityException.class, () -> {
            boardWriteService.dispatch(DeleteBoardByIdCommand.builder()
                    .id(id)
                    .build());
        });
    }

    @Test
    @SneakyThrows
    void handleAddMemberByIdCommand() {
        when(userReadService.query(any(ExistsUserByIdQuery.class))).thenReturn(true);
        when(boardEventStoreRepository.load(id)).thenReturn(Optional.of(boardAggregate));
        doNothing().when(boardEventStoreRepository).save(any(BoardAggregate.class));

        doAnswer(invocation -> {
            CheckedRunnable<?> runnable = invocation.getArgument(1);
            runnable.run();
            return null;
        }).when(lockRegistry).executeLocked(any(), any(CheckedRunnable.class));

        boardWriteService.dispatch(AddMemberByIdCommand.builder()
                .boardId(id)
                .userId(UUID.randomUUID())
                .role(Role.MEMBER)
                .build());

        assertEquals(3, boardAggregate.getMembers().size());

        verify(userReadService, times(1)).query(any(ExistsUserByIdQuery.class));
        verify(boardEventStoreRepository, times(1)).save(any(BoardAggregate.class));
    }

    @Test
    @SneakyThrows
    void handleAddMemberByIdCommandWithUnknownUser() {
        when(userReadService.query(any(ExistsUserByIdQuery.class))).thenReturn(false);

        doAnswer(invocation -> {
            CheckedRunnable<?> runnable = invocation.getArgument(1);
            runnable.run();
            return null;
        }).when(lockRegistry).executeLocked(any(), any(CheckedRunnable.class));

        assertThrows(NoSuchEntityException.class, () -> {
            boardWriteService.dispatch(AddMemberByIdCommand.builder()
                    .boardId(id)
                    .userId(UUID.randomUUID())
                    .role(Role.MEMBER)
                    .build());
        });
    }

    @Test
    @SneakyThrows
    void handleAddMemberByIdCommandWithAlreadyExistingMember() {
        when(userReadService.query(any(ExistsUserByIdQuery.class))).thenReturn(true);
        when(boardEventStoreRepository.load(id)).thenReturn(Optional.of(boardAggregate));

        doAnswer(invocation -> {
            CheckedRunnable<?> runnable = invocation.getArgument(1);
            runnable.run();
            return null;
        }).when(lockRegistry).executeLocked(any(), any(CheckedRunnable.class));

        assertThrows(BoardMemberAlreadyExistsException.class, () -> {
            boardWriteService.dispatch(AddMemberByIdCommand.builder()
                    .boardId(id)
                    .userId(creatorUserId)
                    .role(Role.MEMBER)
                    .build());
        });
    }

    @Test
    @SneakyThrows
    void handleRemoveMemberByIdCommand() {
        when(boardEventStoreRepository.load(id)).thenReturn(Optional.of(boardAggregate));
        doNothing().when(boardEventStoreRepository).save(any(BoardAggregate.class));

        doAnswer(invocation -> {
            CheckedRunnable<?> runnable = invocation.getArgument(1);
            runnable.run();
            return null;
        }).when(lockRegistry).executeLocked(any(), any(CheckedRunnable.class));

        boardWriteService.dispatch(RemoveMemberByIdCommand.builder()
                .boardId(id)
                .memberId(dummyMemberId)
                .build());

        assertEquals(1, boardAggregate.getMembers().size());

        verify(boardEventStoreRepository, times(1)).save(any(BoardAggregate.class));
    }

    @Test
    @SneakyThrows
    void handleRemoveMemberByIdCommandWithMissingMember() {
        when(boardEventStoreRepository.load(id)).thenReturn(Optional.of(boardAggregate));

        doAnswer(invocation -> {
            CheckedRunnable<?> runnable = invocation.getArgument(1);
            runnable.run();
            return null;
        }).when(lockRegistry).executeLocked(any(), any(CheckedRunnable.class));

        assertThrows(NoSuchEntityException.class, () -> {
            boardWriteService.dispatch(RemoveMemberByIdCommand.builder()
                    .boardId(id)
                    .memberId(UUID.randomUUID())
                    .build());
        });
    }

    @Test
    @SneakyThrows
    void handleRemoveMemberByIdCommandRemoveLastAdmin() {
        when(boardEventStoreRepository.load(id)).thenReturn(Optional.of(boardAggregate));

        doAnswer(invocation -> {
            CheckedRunnable<?> runnable = invocation.getArgument(1);
            runnable.run();
            return null;
        }).when(lockRegistry).executeLocked(any(), any(CheckedRunnable.class));

        assertThrows(BoardMustBeAdministrableException.class, () -> {
            boardWriteService.dispatch(RemoveMemberByIdCommand.builder()
                    .boardId(id)
                    .memberId(creatorMemberId)
                    .build());
        });
    }

    @Test
    @SneakyThrows
    void handleUpdateMemberByIdCommand() {
        when(boardEventStoreRepository.load(id)).thenReturn(Optional.of(boardAggregate));
        doNothing().when(boardEventStoreRepository).save(any(BoardAggregate.class));

        doAnswer(invocation -> {
            CheckedRunnable<?> runnable = invocation.getArgument(1);
            runnable.run();
            return null;
        }).when(lockRegistry).executeLocked(any(), any(CheckedRunnable.class));

        boardWriteService.dispatch(UpdateMemberByIdCommand.builder()
                .boardId(id)
                .memberId(dummyMemberId)
                .role(Role.MAINTAINER)
                .build());

        verify(boardEventStoreRepository, times(1)).save(any(BoardAggregate.class));
    }

    @Test
    @SneakyThrows
    void handleUpdateMemberByIdCommandWithMissingMember() {
        when(boardEventStoreRepository.load(id)).thenReturn(Optional.of(boardAggregate));

        doAnswer(invocation -> {
            CheckedRunnable<?> runnable = invocation.getArgument(1);
            runnable.run();
            return null;
        }).when(lockRegistry).executeLocked(any(), any(CheckedRunnable.class));

        assertThrows(NoSuchEntityException.class, () -> {
            boardWriteService.dispatch(UpdateMemberByIdCommand.builder()
                    .boardId(id)
                    .memberId(UUID.randomUUID())
                    .role(Role.MAINTAINER)
                    .build());
        });
    }

    @Test
    @SneakyThrows
    void handleUpdateMemberByIdCommandChangeLastAdminRole() {
        when(boardEventStoreRepository.load(id)).thenReturn(Optional.of(boardAggregate));

        doAnswer(invocation -> {
            CheckedRunnable<?> runnable = invocation.getArgument(1);
            runnable.run();
            return null;
        }).when(lockRegistry).executeLocked(any(), any(CheckedRunnable.class));

        assertThrows(BoardMustBeAdministrableException.class, () -> {
            boardWriteService.dispatch(UpdateMemberByIdCommand.builder()
                    .boardId(id)
                    .memberId(creatorMemberId)
                    .role(Role.MAINTAINER)
                    .build());
        });
    }
}
