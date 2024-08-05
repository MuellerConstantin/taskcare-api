package de.x1c1b.taskcare.api.core.common.domain;

import java.io.Serializable;
import java.util.Optional;

/**
 * Simple data access adapter for persisting domain entities.
 *
 * @param <ID> Type of entity's identifier.
 * @param <E>  Type of entity.
 */
public interface Repository<ID extends Serializable, E> {

    Optional<E> findById(ID id);

    Page<E> findAll(FilterSettings filterSettings, PageSettings pageSettings);

    boolean existsById(ID id);

    boolean deleteById(ID id);

    void save(E aggregate);
}
