package de.mueller_constantin.taskcare.api.core.board.application;

import de.mueller_constantin.taskcare.api.core.board.application.persistence.ComponentReadModelRepository;
import de.mueller_constantin.taskcare.api.core.board.domain.ComponentProjection;
import de.mueller_constantin.taskcare.api.core.common.application.ApplicationService;
import de.mueller_constantin.taskcare.api.core.common.application.NoSuchEntityException;
import de.mueller_constantin.taskcare.api.core.common.domain.Page;
import de.mueller_constantin.taskcare.api.core.common.domain.PageInfo;
import de.mueller_constantin.taskcare.api.core.board.application.persistence.BoardReadModelRepository;
import de.mueller_constantin.taskcare.api.core.board.application.persistence.MemberReadModelRepository;
import de.mueller_constantin.taskcare.api.core.board.application.persistence.StatusReadModelRepository;
import de.mueller_constantin.taskcare.api.core.board.application.query.*;
import de.mueller_constantin.taskcare.api.core.board.domain.BoardProjection;
import de.mueller_constantin.taskcare.api.core.board.domain.MemberProjection;
import de.mueller_constantin.taskcare.api.core.board.domain.StatusProjection;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BoardReadService implements ApplicationService {
    private final BoardReadModelRepository boardReadModelRepository;
    private final MemberReadModelRepository memberReadModelRepository;
    private final StatusReadModelRepository statusReadModelRepository;
    private final ComponentReadModelRepository componentReadModelRepository;

    public BoardReadService(BoardReadModelRepository boardReadModelRepository,
                            MemberReadModelRepository memberReadModelRepository,
                            StatusReadModelRepository statusReadModelRepository,
                            ComponentReadModelRepository componentReadModelRepository) {
        this.boardReadModelRepository = boardReadModelRepository;
        this.memberReadModelRepository = memberReadModelRepository;
        this.statusReadModelRepository = statusReadModelRepository;
        this.componentReadModelRepository = componentReadModelRepository;
    }

    public BoardProjection query(FindBoardByIdQuery query) {
        return boardReadModelRepository.findById(query.getId())
                .orElseThrow(NoSuchEntityException::new);
    }

    public Page<BoardProjection> query(FindAllBoardsUserIsMemberQuery query) {
        return boardReadModelRepository.findAllUserIsMember(query.getUserId(), PageInfo.builder()
                .page(query.getPage())
                .perPage(query.getPerPage())
                .build(), query.getSearch());
    }

    public Page<BoardProjection> query(FindAllBoardsQuery query) {
        return boardReadModelRepository.findAll(PageInfo.builder()
                .page(query.getPage())
                .perPage(query.getPerPage())
                .build(), query.getSearch());
    }

    public MemberProjection query(FindMemberByIdAndBoardIdQuery query) {
        return memberReadModelRepository.findByIdAndBoardId(query.getId(), query.getBoardId())
                .orElseThrow(NoSuchEntityException::new);
    }

    public Page<MemberProjection> query(FindAllMembersByBoardIdQuery query) {
        return memberReadModelRepository.findAllByBoardId(query.getBoardId(), PageInfo.builder()
                .page(query.getPage())
                .perPage(query.getPerPage())
                .build(), query.getSearch());
    }

    public MemberProjection query(FindMemberByUserIdAndBoardIdQuery query) {
        return memberReadModelRepository.findByUserIdAndBoardId(query.getUserId(), query.getBoardId())
                .orElseThrow(NoSuchEntityException::new);
    }

    public boolean query(ExistsMemberByUserIdAndBoardIdQuery query) {
        return memberReadModelRepository.existsByUserIdAndBoardId(query.getUserId(), query.getBoardId());
    }

    public boolean query(ExistsMemberByMemberIdAndBoardIdQuery query) {
        return memberReadModelRepository.existsByIdAndBoardId(query.getMemberId(), query.getBoardId());
    }

    public boolean query(ExistsStatusByStatusIdAndBoardIdQuery query) {
        return statusReadModelRepository.existsByIdAndBoardId(query.getStatusId(), query.getBoardId());
    }

    public boolean query(ExistsComponentByComponentIdAndBoardIdQuery query) {
        return componentReadModelRepository.existsByIdAndBoardId(query.getComponentId(), query.getBoardId());
    }

    public boolean query(ExistsBoardByIdQuery query) {
        return boardReadModelRepository.existsById(query.getId());
    }

    public Page<StatusProjection> query(FindAllStatusesByBoardIdQuery query) {
        return statusReadModelRepository.findAllByBoardId(query.getBoardId(), PageInfo.builder()
                .page(query.getPage())
                .perPage(query.getPerPage())
                .build(), query.getSearch());
    }

    public StatusProjection query(FindStatusByIdAndBoardIdQuery query) {
        return statusReadModelRepository.findByIdAndBoardId(query.getId(), query.getBoardId())
                .orElseThrow(NoSuchEntityException::new);
    }

    public Page<ComponentProjection> query(FindAllComponentsByBoardIdQuery query) {
        return componentReadModelRepository.findAllByBoardId(query.getBoardId(), PageInfo.builder()
                .page(query.getPage())
                .perPage(query.getPerPage())
                .build(), query.getSearch());
    }

    public ComponentProjection query(FindComponentByIdAndBoardIdQuery query) {
        return componentReadModelRepository.findByIdAndBoardId(query.getId(), query.getBoardId())
                .orElseThrow(NoSuchEntityException::new);
    }
}
