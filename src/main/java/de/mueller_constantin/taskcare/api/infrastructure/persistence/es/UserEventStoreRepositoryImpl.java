package de.mueller_constantin.taskcare.api.infrastructure.persistence.es;

import de.mueller_constantin.taskcare.api.core.common.domain.Page;
import de.mueller_constantin.taskcare.api.core.common.domain.PageInfo;
import de.mueller_constantin.taskcare.api.core.user.domain.UserAggregate;
import de.mueller_constantin.taskcare.api.core.user.domain.UserProjection;
import de.mueller_constantin.taskcare.api.core.user.application.persistence.UserEventStoreRepository;
import de.mueller_constantin.taskcare.api.infrastructure.persistence.crud.UserCrudRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@Transactional
@RequiredArgsConstructor
public class UserEventStoreRepositoryImpl implements UserEventStoreRepository {
    private final EventStore eventStore;
    private final UserCrudRepository userCrudRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void save(UserAggregate aggregate) {
        eventStore.saveAggregate(aggregate);

        // Synchronize read model with event store

        if(aggregate.isDeleted()) {
            userCrudRepository.deleteById(aggregate.getId());
        } else {
            UserProjection projection = UserProjection.builder()
                    .id(aggregate.getId())
                    .username(aggregate.getUsername())
                    .password(aggregate.getPassword())
                    .displayName(aggregate.getDisplayName())
                    .identityProvider(aggregate.getIdentityProvider())
                    .role(aggregate.getRole())
                    .locked(aggregate.isLocked())
                    .build();

            userCrudRepository.save(projection);
        }

        aggregate.getUncommittedEvents().forEach(applicationEventPublisher::publishEvent);
        aggregate.commit();
    }

    @Override
    public Optional<UserAggregate> load(UUID aggregateId) {
        return eventStore.loadAggregate(aggregateId, UserAggregate.class, null);
    }

    @Override
    public Optional<UserAggregate> load(UUID aggregateId, Integer version) {
        return eventStore.loadAggregate(aggregateId, UserAggregate.class, version);
    }
}
