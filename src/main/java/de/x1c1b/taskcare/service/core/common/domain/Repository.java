package de.x1c1b.taskcare.service.core.common.domain;

import java.util.Optional;

/**
 * Simple data access adapter for persisting domain entities.
 *
 * @param <ID> Type of entity's identifier.
 * @param <E>  Type of entity.
 */
public interface Repository<ID, E> {

    Optional<E> findById(ID id);

    Page<E> findAll(PageSettings pageSettings);

    boolean existsById(ID id);

    boolean deleteById(ID id);

    void save(E model);
}
