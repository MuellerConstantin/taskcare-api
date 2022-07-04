package de.x1c1b.taskcare.service.core.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private PageInfo info;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class PageInfo {
        private int page;
        private int perPage;
        private long totalElements;
        private long totalPages;
    }
}
