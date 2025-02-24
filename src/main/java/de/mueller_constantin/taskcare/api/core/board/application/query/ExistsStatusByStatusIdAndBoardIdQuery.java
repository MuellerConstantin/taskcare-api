package de.mueller_constantin.taskcare.api.core.board.application.query;

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
public class ExistsStatusByStatusIdAndBoardIdQuery implements Query {
    private UUID statusId;
    private UUID boardId;
}
