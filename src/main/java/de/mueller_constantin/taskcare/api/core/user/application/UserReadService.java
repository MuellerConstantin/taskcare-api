package de.mueller_constantin.taskcare.api.core.user.application;

import de.mueller_constantin.taskcare.api.core.common.application.ApplicationService;
import de.mueller_constantin.taskcare.api.core.common.application.NoSuchEntityException;
import de.mueller_constantin.taskcare.api.core.common.domain.Page;
import de.mueller_constantin.taskcare.api.core.common.domain.PageInfo;
import de.mueller_constantin.taskcare.api.core.user.application.persistence.UserReadModelRepository;
import de.mueller_constantin.taskcare.api.core.user.application.query.ExistsUserByIdQuery;
import de.mueller_constantin.taskcare.api.core.user.application.query.FindAllUsersQuery;
import de.mueller_constantin.taskcare.api.core.user.application.query.FindUserByIdQuery;
import de.mueller_constantin.taskcare.api.core.user.application.query.FindUserByUsernameQuery;
import de.mueller_constantin.taskcare.api.core.user.domain.UserProjection;

public class UserReadService implements ApplicationService {
    private final UserReadModelRepository userReadModelRepository;

    public UserReadService(UserReadModelRepository userReadModelRepository) {
        this.userReadModelRepository = userReadModelRepository;
    }

    public UserProjection query(FindUserByIdQuery query) {
        return userReadModelRepository.findById(query.getId())
                .orElseThrow(NoSuchEntityException::new);
    }

    public UserProjection query(FindUserByUsernameQuery query) {
        return userReadModelRepository.findByUsername(query.getUsername())
                .orElseThrow(NoSuchEntityException::new);
    }

    public Page<UserProjection> query(FindAllUsersQuery query) {
        return userReadModelRepository.findAll(PageInfo.builder()
                .page(query.getPage())
                .perPage(query.getPerPage())
                .build(), query.getSearch());
    }

    public boolean query(ExistsUserByIdQuery query) {
        return userReadModelRepository.existsById(query.getId());
    }
}
