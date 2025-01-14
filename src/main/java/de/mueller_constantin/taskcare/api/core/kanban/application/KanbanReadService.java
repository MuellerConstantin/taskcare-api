package de.mueller_constantin.taskcare.api.core.kanban.application;

import de.mueller_constantin.taskcare.api.core.common.application.ApplicationService;
import de.mueller_constantin.taskcare.api.core.common.application.NoSuchEntityException;
import de.mueller_constantin.taskcare.api.core.common.domain.Page;
import de.mueller_constantin.taskcare.api.core.common.domain.PageInfo;
import de.mueller_constantin.taskcare.api.core.kanban.application.persistence.BoardReadModelRepository;
import de.mueller_constantin.taskcare.api.core.kanban.application.persistence.MemberReadModelRepository;
import de.mueller_constantin.taskcare.api.core.kanban.application.persistence.StatusReadModelRepository;
import de.mueller_constantin.taskcare.api.core.kanban.application.query.*;
import de.mueller_constantin.taskcare.api.core.kanban.domain.BoardProjection;
import de.mueller_constantin.taskcare.api.core.kanban.domain.MemberProjection;
import de.mueller_constantin.taskcare.api.core.kanban.domain.StatusProjection;

public class KanbanReadService implements ApplicationService {
    private final BoardReadModelRepository boardReadModelRepository;
    private final MemberReadModelRepository memberReadModelRepository;
    private final StatusReadModelRepository statusReadModelRepository;

    public KanbanReadService(BoardReadModelRepository boardReadModelRepository,
                             MemberReadModelRepository memberReadModelRepository,
                             StatusReadModelRepository statusReadModelRepository) {
        this.boardReadModelRepository = boardReadModelRepository;
        this.memberReadModelRepository = memberReadModelRepository;
        this.statusReadModelRepository = statusReadModelRepository;
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

    public MemberProjection query(FindMemberByIdAndBoardIdQuery query) {
        return memberReadModelRepository.findByIdAndBoardId(query.getId(), query.getBoardId())
                .orElseThrow(NoSuchEntityException::new);
    }

    public Page<MemberProjection> query(FindAllMembersByBoardIdQuery query) {
        return memberReadModelRepository.findAllByBoardId(query.getBoardId(), PageInfo.builder()
                .page(query.getPage())
                .perPage(query.getPerPage())
                .build());
    }

    public MemberProjection query(FindMemberByUserIdAndBoardIdQuery query) {
        return memberReadModelRepository.findByUserIdAndBoardId(query.getUserId(), query.getBoardId())
                .orElseThrow(NoSuchEntityException::new);
    }

    public boolean query(ExistsMemberByUserIdAndBoardIdQuery query) {
        return memberReadModelRepository.existsByUserIdAndBoardId(query.getUserId(), query.getBoardId());
    }

    public Page<StatusProjection> query(FindAllStatusesByBoardIdQuery query) {
        return statusReadModelRepository.findAllByBoardId(query.getBoardId(), PageInfo.builder()
                .page(query.getPage())
                .perPage(query.getPerPage())
                .build());
    }

    public StatusProjection query(FindStatusByIdAndBoardIdQuery query) {
        return statusReadModelRepository.findByIdAndBoardId(query.getId(), query.getBoardId())
                .orElseThrow(NoSuchEntityException::new);
    }
}
