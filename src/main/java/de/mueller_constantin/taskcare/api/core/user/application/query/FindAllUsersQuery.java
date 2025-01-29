package de.mueller_constantin.taskcare.api.core.user.application.query;

import de.mueller_constantin.taskcare.api.core.common.application.Query;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class FindAllUsersQuery implements Query {
    private int page;
    private int perPage;
    private String search;
}
