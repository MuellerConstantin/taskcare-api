package de.mueller_constantin.taskcare.api.core.user.application.persistence;

import de.mueller_constantin.taskcare.api.core.common.application.DomainRepository;
import de.mueller_constantin.taskcare.api.core.user.domain.UserAggregate;

public interface UserDomainRepository extends DomainRepository<UserAggregate> {
}
