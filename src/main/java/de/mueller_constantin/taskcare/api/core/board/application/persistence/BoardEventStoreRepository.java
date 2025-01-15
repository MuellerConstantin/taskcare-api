package de.mueller_constantin.taskcare.api.core.board.application.persistence;

import de.mueller_constantin.taskcare.api.core.common.application.EventStoreRepository;
import de.mueller_constantin.taskcare.api.core.board.domain.BoardAggregate;

public interface BoardEventStoreRepository extends EventStoreRepository<BoardAggregate> {
}
