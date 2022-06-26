package de.x1c1b.taskcare.service.core.board.application.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class HasBoardMemberQuery {

    private UUID id;
    private String username;
}
