package de.x1c1b.taskcare.service.core.common.domain;

import lombok.*;

/**
 * Controls paging settings when accessing a collection.
 *
 * @see Page
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PageSettings {

    @Builder.Default
    private int page = 0;

    @Builder.Default
    private int perPage = 25;

    @Builder.Default
    private String sortBy = null;

    @Builder.Default
    private SortDirection sortDirection = null;

    @AllArgsConstructor
    @Getter
    @ToString
    public enum SortDirection {
        ASC("ASC"),
        DESC("DESC");

        private final String name;
    }
}
