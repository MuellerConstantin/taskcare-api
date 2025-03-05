package de.mueller_constantin.taskcare.api.core.task.application.query;

import de.mueller_constantin.taskcare.api.core.common.application.Query;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class FindAllTasksByBoardIdAndAssigneeIdQuery implements Query {
    private UUID boardId;
    private UUID assigneeId;
    private int page;
    private int perPage;
}
