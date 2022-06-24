package de.x1c1b.taskcare.service.core.board.application.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class HasBoardMemberQuery {

    private String username;
}
