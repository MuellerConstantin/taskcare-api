package de.mueller_constantin.taskcare.api.presentation.rest.v1.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PageDto<T> {
    private List<T> content;
    private PageInfoDto info;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class PageInfoDto {
        private int page;
        private int perPage;
        private long totalElements;
        private long totalPages;
    }
}
