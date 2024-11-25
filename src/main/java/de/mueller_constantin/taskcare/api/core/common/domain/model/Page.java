package de.mueller_constantin.taskcare.api.core.common.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * A data page that contains a section of a collection. This allows large
 * collections to be loaded page by page, thus saving payload.
 *
 * @param <T> Type of entity in collection.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Page<T> {
    private List<T> content;
    private PageInfo info;
}
