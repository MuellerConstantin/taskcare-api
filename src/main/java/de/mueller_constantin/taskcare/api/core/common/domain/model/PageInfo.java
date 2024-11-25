package de.mueller_constantin.taskcare.api.core.common.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PageInfo {
    @Builder.Default
    private int page = 0;

    @Builder.Default
    private int perPage = 10;

    private long totalElements;
    private long totalPages;
}
