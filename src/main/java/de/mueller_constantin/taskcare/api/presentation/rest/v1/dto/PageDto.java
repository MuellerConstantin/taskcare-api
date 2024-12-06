package de.mueller_constantin.taskcare.api.presentation.rest.v1.dto;

import com.fasterxml.jackson.annotation.JsonView;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.view.DefaultJsonViews;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@JsonView(DefaultJsonViews.User.class)
public class PageDto<T> {
    private List<T> content;
    private PageInfoDto info;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    @JsonView(DefaultJsonViews.User.class)
    public static class PageInfoDto {
        private int page;
        private int perPage;
        private long totalElements;
        private long totalPages;
    }
}
