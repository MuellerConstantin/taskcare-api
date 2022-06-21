package de.x1c1b.taskcare.service.core.user.application.query;

import de.x1c1b.taskcare.service.core.common.domain.PageSettings;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class FindAllUsersQuery {

    @Builder.Default
    private int page = 0;

    @Builder.Default
    private int perPage = 25;

    @Builder.Default
    private String sortBy = null;

    @Builder.Default
    private PageSettings.SortDirection sortDirection = null;
}
