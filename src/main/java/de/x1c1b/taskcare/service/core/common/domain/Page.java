package de.x1c1b.taskcare.service.core.common.domain;

import lombok.*;

import java.util.List;

/**
 * A data page that contains a section of a collection. This allows large
 * collections to be loaded page by page, thus saving payload.
 *
 * @param <E> Type of entity in collection.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Page<E> {

    private List<E> content;
    private int page;
    private int perPage;
    private long totalElements;
    private long totalPages;
    private String sortBy;
    private SortDirection sortDirection;

    @AllArgsConstructor
    @Getter
    @ToString
    public enum SortDirection {
        ASC("ASC"),
        DESC("DESC");

        private final String name;
    }
}
