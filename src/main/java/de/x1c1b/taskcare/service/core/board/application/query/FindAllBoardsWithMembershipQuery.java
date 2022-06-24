package de.x1c1b.taskcare.service.core.board.application.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class FindAllBoardsWithMembershipQuery {

    @Builder.Default
    private int page = 0;

    @Builder.Default
    private int perPage = 25;

    private String username;
}
