package de.x1c1b.taskcare.api.presentation.rest.v1.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PageDTO<T> {

    private List<T> content;
    private PageInfoDTO info;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class PageInfoDTO {
        private int page;
        private int perPage;
        private long totalElements;
        private long totalPages;
    }
}
