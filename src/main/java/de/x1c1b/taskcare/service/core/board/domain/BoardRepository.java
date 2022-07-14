package de.x1c1b.taskcare.service.core.board.domain;

import de.x1c1b.taskcare.service.core.common.domain.FilterSettings;
import de.x1c1b.taskcare.service.core.common.domain.Page;
import de.x1c1b.taskcare.service.core.common.domain.PageSettings;
import de.x1c1b.taskcare.service.core.common.domain.Repository;

import java.util.List;
import java.util.UUID;

public interface BoardRepository extends Repository<UUID, Board> {

    boolean hasMember(UUID id, String username);

    boolean hasMemberWithRole(UUID id, String username, Role role);

    boolean hasMemberWithAnyRole(UUID id, String username, List<Role> roles);

    Page<Board> findAllWithMembership(String username, FilterSettings filterSettings, PageSettings pageSettings);
}
