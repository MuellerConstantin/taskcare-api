package de.x1c1b.taskcare.api.core.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
