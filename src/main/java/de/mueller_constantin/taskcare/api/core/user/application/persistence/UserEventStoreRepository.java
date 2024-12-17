package de.mueller_constantin.taskcare.api.core.user.application.persistence;

import de.mueller_constantin.taskcare.api.core.common.application.EventStoreRepository;
import de.mueller_constantin.taskcare.api.core.user.domain.UserAggregate;

public interface UserEventStoreRepository extends EventStoreRepository<UserAggregate> {
}
