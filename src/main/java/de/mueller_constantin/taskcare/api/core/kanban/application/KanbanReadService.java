package de.mueller_constantin.taskcare.api.core.kanban.application;

import de.mueller_constantin.taskcare.api.core.common.application.ApplicationService;
import de.mueller_constantin.taskcare.api.core.common.application.NoSuchEntityException;
import de.mueller_constantin.taskcare.api.core.common.domain.Page;
import de.mueller_constantin.taskcare.api.core.common.domain.PageInfo;
import de.mueller_constantin.taskcare.api.core.kanban.application.persistence.BoardReadModelRepository;
import de.mueller_constantin.taskcare.api.core.kanban.application.query.FindAllBoardsQuery;
import de.mueller_constantin.taskcare.api.core.kanban.application.query.FindAllBoardsUserIsMemberQuery;
import de.mueller_constantin.taskcare.api.core.kanban.application.query.FindBoardByIdQuery;
import de.mueller_constantin.taskcare.api.core.kanban.domain.BoardProjection;

public class KanbanReadService implements ApplicationService {
    private final BoardReadModelRepository boardReadModelRepository;

    public KanbanReadService(BoardReadModelRepository boardReadModelRepository) {
        this.boardReadModelRepository = boardReadModelRepository;
    }

    public BoardProjection query(FindBoardByIdQuery query) {
        return boardReadModelRepository.findById(query.getId())
                .orElseThrow(NoSuchEntityException::new);
    }

    public Page<BoardProjection> query(FindAllBoardsUserIsMemberQuery query) {
        return boardReadModelRepository.findAllUserIsMember(query.getUserId(), PageInfo.builder()
                .page(query.getPage())
                .perPage(query.getPerPage())
                .build());
    }

    public Page<BoardProjection> query(FindAllBoardsQuery query) {
        return boardReadModelRepository.findAll(PageInfo.builder()
                .page(query.getPage())
                .perPage(query.getPerPage())
                .build());
    }
}
