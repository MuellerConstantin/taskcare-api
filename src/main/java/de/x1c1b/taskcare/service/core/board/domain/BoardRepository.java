package de.x1c1b.taskcare.service.core.board.domain;

import de.x1c1b.taskcare.service.core.common.domain.Page;
import de.x1c1b.taskcare.service.core.common.domain.PageSettings;
import de.x1c1b.taskcare.service.core.common.domain.Repository;

import java.util.UUID;

public interface BoardRepository extends Repository<UUID, Board> {

    boolean hasMember(String username);

    boolean hasMemberWithRole(String username, Role role);

    Page<Board> findAllWithMembership(String username, PageSettings pageSettings);
}
