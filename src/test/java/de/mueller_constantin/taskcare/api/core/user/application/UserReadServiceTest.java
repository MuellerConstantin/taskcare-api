package de.mueller_constantin.taskcare.api.core.user.application;

import de.mueller_constantin.taskcare.api.core.common.application.NoSuchEntityException;
import de.mueller_constantin.taskcare.api.core.common.domain.Page;
import de.mueller_constantin.taskcare.api.core.common.domain.PageInfo;
import de.mueller_constantin.taskcare.api.core.user.application.persistence.UserReadModelRepository;
import de.mueller_constantin.taskcare.api.core.user.application.query.FindAllUsersQuery;
import de.mueller_constantin.taskcare.api.core.user.application.query.FindUserByIdQuery;
import de.mueller_constantin.taskcare.api.core.user.application.query.FindUserByUsernameQuery;
import de.mueller_constantin.taskcare.api.core.user.domain.IdentityProvider;
import de.mueller_constantin.taskcare.api.core.user.domain.Role;
import de.mueller_constantin.taskcare.api.core.user.domain.UserAggregate;
import de.mueller_constantin.taskcare.api.core.user.domain.UserProjection;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserReadServiceTest {
    @Mock
    private UserReadModelRepository userReadModelRepository;

    @InjectMocks
    private UserReadService userReadService;

    private UUID id;
    private UserProjection userProjection;

    @BeforeEach
    void setUp() {
        this.id = UUID.randomUUID();

        this.userProjection = UserProjection.builder()
                .id(this.id)
                .username("maxi123")
                .password("Abc123")
                .displayName("Maximilian Mustermann")
                .role(Role.USER)
                .identityProvider(IdentityProvider.LOCAL)
                .locked(false)
                .build();
    }

    @Test
    void handleFindUserByIdQuery() {
        when(userReadModelRepository.findById(id)).thenReturn(Optional.of(userProjection));

        UserProjection result = userReadService.query(FindUserByIdQuery.builder()
                .id(id)
                .build());

        assertEquals(userProjection, result);
    }

    @Test
    void handleFindUserByIdQueryUnknownId() {
        when(userReadModelRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NoSuchEntityException.class, () -> {
            userReadService.query(FindUserByIdQuery.builder()
                    .id(id)
                    .build());
        });
    }

    @Test
    void handleFindUserByUsernameQuery() {
        when(userReadModelRepository.findByUsername(userProjection.getUsername())).thenReturn(Optional.of(userProjection));

        UserProjection result = userReadService.query(FindUserByUsernameQuery.builder()
                .username(userProjection.getUsername())
                .build());

        assertEquals(userProjection, result);
    }

    @Test
    void handleFindUserByUsernameQueryUnknownUsername() {
        when(userReadModelRepository.findByUsername("erika123")).thenReturn(Optional.empty());

        assertThrows(NoSuchEntityException.class, () -> {
            userReadService.query(FindUserByUsernameQuery.builder()
                    .username("erika123")
                    .build());
        });
    }

    @Test
    void handleFindAllUsersQuery() {
        when(userReadModelRepository.findAll(any(PageInfo.class))).thenReturn(Page.<UserProjection>builder()
                .content(List.of(userProjection))
                .info(PageInfo.builder()
                        .page(0)
                        .perPage(10)
                        .build())
                .build());

        Page<UserProjection> result = userReadService.query(FindAllUsersQuery.builder()
                .page(0)
                .perPage(10)
                .build());

        assertEquals(userProjection, result.getContent().get(0));
        verify(userReadModelRepository, times(1)).findAll(any(PageInfo.class));
    }
}