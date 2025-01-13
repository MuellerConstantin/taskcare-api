package de.mueller_constantin.taskcare.api.infrastructure.persistence;

import de.mueller_constantin.taskcare.api.core.common.domain.Page;
import de.mueller_constantin.taskcare.api.core.common.domain.PageInfo;
import de.mueller_constantin.taskcare.api.core.user.domain.UserAggregate;
import de.mueller_constantin.taskcare.api.core.user.domain.UserProjection;
import de.mueller_constantin.taskcare.api.core.user.application.persistence.UserEventStoreRepository;
import de.mueller_constantin.taskcare.api.core.user.application.persistence.UserReadModelRepository;
import de.mueller_constantin.taskcare.api.infrastructure.persistence.crud.UserCrudRepository;
import de.mueller_constantin.taskcare.api.infrastructure.persistence.es.EventStore;
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
public class UserRepository implements UserEventStoreRepository, UserReadModelRepository {
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

    @Override
    public Optional<UserProjection> findByUsername(String username) {
        return userCrudRepository.findByUsername(username);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userCrudRepository.existsByUsername(username);
    }

    @Override
    public Optional<UserProjection> findById(UUID id) {
        return userCrudRepository.findById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return userCrudRepository.existsById(id);
    }

    @Override
    public List<UserProjection> findAll() {
        return userCrudRepository.findAll();
    }

    @Override
    public Page<UserProjection> findAll(PageInfo pageInfo) {
        return userCrudRepository.findAll(pageInfo);
    }
}
