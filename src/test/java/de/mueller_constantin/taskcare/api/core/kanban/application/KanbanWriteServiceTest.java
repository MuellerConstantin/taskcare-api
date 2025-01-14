package de.mueller_constantin.taskcare.api.core.kanban.application;

import de.mueller_constantin.taskcare.api.core.common.application.NoSuchEntityException;
import de.mueller_constantin.taskcare.api.core.common.application.event.DomainEventBus;
import de.mueller_constantin.taskcare.api.core.common.domain.Entity;
import de.mueller_constantin.taskcare.api.core.kanban.application.command.*;
import de.mueller_constantin.taskcare.api.core.kanban.application.persistence.KanbanEventStoreRepository;
import de.mueller_constantin.taskcare.api.core.kanban.application.persistence.BoardReadModelRepository;
import de.mueller_constantin.taskcare.api.core.kanban.domain.*;
import de.mueller_constantin.taskcare.api.core.user.application.UserReadService;
import de.mueller_constantin.taskcare.api.core.user.application.query.ExistsUserByIdQuery;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KanbanWriteServiceTest {
    @Mock
    private KanbanEventStoreRepository kanbanEventStoreRepository;

    @Mock
    private BoardReadModelRepository boardReadModelRepository;

    @Mock
    private UserReadService userReadService;

    @Mock
    private DomainEventBus domainEventBus;

    @Mock
    private Validator validator;

    @InjectMocks
    private KanbanWriteService kanbanWriteService;

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
            kanbanWriteService.dispatch(CreateBoardCommand.builder()
                    .name("Second Test Board")
                    .description("Lorem ipsum dolor sit amet, consetetur sadipscing elitr")
                    .creatorId(UUID.randomUUID())
                    .build());
        });
    }

    @Test
    void handleCreateBoardCommand() {
        when(userReadService.query(any(ExistsUserByIdQuery.class))).thenReturn(true);
        doNothing().when(kanbanEventStoreRepository).save(any(BoardAggregate.class));

        kanbanWriteService.dispatch(CreateBoardCommand.builder()
                .name("Second Test Board")
                .description("Lorem ipsum dolor sit amet, consetetur sadipscing elitr")
                .creatorId(UUID.randomUUID())
                .build());

        verify(userReadService, times(1)).query(any(ExistsUserByIdQuery.class));
        verify(kanbanEventStoreRepository, times(1)).save(any(BoardAggregate.class));
    }

    @Test
    void handleUpdateBoardByIdCommand() {
        when(kanbanEventStoreRepository.load(id)).thenReturn(Optional.of(boardAggregate));
        doNothing().when(kanbanEventStoreRepository).save(any(BoardAggregate.class));

        kanbanWriteService.dispatch(UpdateBoardByIdCommand.builder()
                .id(id)
                .name("Another Test Board")
                .build());

        verify(kanbanEventStoreRepository, times(1)).load(id);
        verify(kanbanEventStoreRepository, times(1)).save(any(BoardAggregate.class));
    }

    @Test
    void handleUpdateBoardByIdCommandUnknownId() {
        when(kanbanEventStoreRepository.load(id)).thenReturn(Optional.empty());

        assertThrows(NoSuchEntityException.class, () -> {
            kanbanWriteService.dispatch(UpdateBoardByIdCommand.builder()
                    .id(id)
                    .name("Another Test Board")
                    .build());
        });
    }

    @Test
    void handleDeleteBoardByIdCommand() {
        when(kanbanEventStoreRepository.load(id)).thenReturn(Optional.of(boardAggregate));
        doNothing().when(kanbanEventStoreRepository).save(any(BoardAggregate.class));

        kanbanWriteService.dispatch(DeleteBoardByIdCommand.builder()
                .id(id)
                .build());

        verify(kanbanEventStoreRepository, times(1)).load(id);
        verify(kanbanEventStoreRepository, times(1)).save(any(BoardAggregate.class));
    }

    @Test
    void handleDeleteBoardByIdCommandUnknownId() {
        when(kanbanEventStoreRepository.load(id)).thenReturn(Optional.empty());

        assertThrows(NoSuchEntityException.class, () -> {
            kanbanWriteService.dispatch(DeleteBoardByIdCommand.builder()
                    .id(id)
                    .build());
        });
    }

    @Test
    void handleAddMemberByIdCommand() {
        when(userReadService.query(any(ExistsUserByIdQuery.class))).thenReturn(true);
        when(kanbanEventStoreRepository.load(id)).thenReturn(Optional.of(boardAggregate));
        doNothing().when(kanbanEventStoreRepository).save(any(BoardAggregate.class));

        kanbanWriteService.dispatch(AddMemberByIdCommand.builder()
                .boardId(id)
                .userId(UUID.randomUUID())
                .role(Role.MEMBER)
                .build());

        assertEquals(3, boardAggregate.getMembers().size());

        verify(userReadService, times(1)).query(any(ExistsUserByIdQuery.class));
        verify(kanbanEventStoreRepository, times(1)).save(any(BoardAggregate.class));
    }

    @Test
    void handleAddMemberByIdCommandWithUnknownUser() {
        when(userReadService.query(any(ExistsUserByIdQuery.class))).thenReturn(false);

        assertThrows(NoSuchEntityException.class, () -> {
            kanbanWriteService.dispatch(AddMemberByIdCommand.builder()
                    .boardId(id)
                    .userId(UUID.randomUUID())
                    .role(Role.MEMBER)
                    .build());
        });
    }

    @Test
    void handleAddMemberByIdCommandWithAlreadyExistingMember() {
        when(userReadService.query(any(ExistsUserByIdQuery.class))).thenReturn(true);
        when(kanbanEventStoreRepository.load(id)).thenReturn(Optional.of(boardAggregate));

        assertThrows(BoardMemberAlreadyExistsException.class, () -> {
            kanbanWriteService.dispatch(AddMemberByIdCommand.builder()
                    .boardId(id)
                    .userId(creatorUserId)
                    .role(Role.MEMBER)
                    .build());
        });
    }

    @Test
    void handleRemoveMemberByIdCommand() {
        when(kanbanEventStoreRepository.load(id)).thenReturn(Optional.of(boardAggregate));
        doNothing().when(kanbanEventStoreRepository).save(any(BoardAggregate.class));

        kanbanWriteService.dispatch(RemoveMemberByIdCommand.builder()
                .boardId(id)
                .memberId(dummyMemberId)
                .build());

        assertEquals(1, boardAggregate.getMembers().size());

        verify(kanbanEventStoreRepository, times(1)).save(any(BoardAggregate.class));
    }

    @Test
    void handleRemoveMemberByIdCommandWithMissingMember() {
        when(kanbanEventStoreRepository.load(id)).thenReturn(Optional.of(boardAggregate));

        assertThrows(NoSuchEntityException.class, () -> {
            kanbanWriteService.dispatch(RemoveMemberByIdCommand.builder()
                    .boardId(id)
                    .memberId(UUID.randomUUID())
                    .build());
        });
    }

    @Test
    void handleRemoveMemberByIdCommandRemoveLastAdmin() {
        when(kanbanEventStoreRepository.load(id)).thenReturn(Optional.of(boardAggregate));

        assertThrows(BoardMustBeAdministrableException.class, () -> {
            kanbanWriteService.dispatch(RemoveMemberByIdCommand.builder()
                    .boardId(id)
                    .memberId(creatorMemberId)
                    .build());
        });
    }

    @Test
    void handleUpdateMemberByIdCommand() {
        when(kanbanEventStoreRepository.load(id)).thenReturn(Optional.of(boardAggregate));
        doNothing().when(kanbanEventStoreRepository).save(any(BoardAggregate.class));

        kanbanWriteService.dispatch(UpdateMemberByIdCommand.builder()
                .boardId(id)
                .memberId(dummyMemberId)
                .role(Role.MAINTAINER)
                .build());

        verify(kanbanEventStoreRepository, times(1)).save(any(BoardAggregate.class));
    }

    @Test
    void handleUpdateMemberByIdCommandWithMissingMember() {
        when(kanbanEventStoreRepository.load(id)).thenReturn(Optional.of(boardAggregate));

        assertThrows(NoSuchEntityException.class, () -> {
            kanbanWriteService.dispatch(UpdateMemberByIdCommand.builder()
                    .boardId(id)
                    .memberId(UUID.randomUUID())
                    .role(Role.MAINTAINER)
                    .build());
        });
    }

    @Test
    void handleUpdateMemberByIdCommandChangeLastAdminRole() {
        when(kanbanEventStoreRepository.load(id)).thenReturn(Optional.of(boardAggregate));

        assertThrows(BoardMustBeAdministrableException.class, () -> {
            kanbanWriteService.dispatch(UpdateMemberByIdCommand.builder()
                    .boardId(id)
                    .memberId(creatorMemberId)
                    .role(Role.MAINTAINER)
                    .build());
        });
    }
}
