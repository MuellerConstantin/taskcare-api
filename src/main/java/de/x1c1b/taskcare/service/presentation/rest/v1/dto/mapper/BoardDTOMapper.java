package de.x1c1b.taskcare.service.presentation.rest.v1.dto.mapper;

import de.x1c1b.taskcare.service.core.board.application.command.CreateBoardCommand;
import de.x1c1b.taskcare.service.core.board.application.command.UpdateBoardByIdCommand;
import de.x1c1b.taskcare.service.core.board.domain.Board;
import de.x1c1b.taskcare.service.core.common.domain.Page;
import de.x1c1b.taskcare.service.presentation.rest.v1.dto.BoardDTO;
import de.x1c1b.taskcare.service.presentation.rest.v1.dto.CreateBoardDTO;
import de.x1c1b.taskcare.service.presentation.rest.v1.dto.PageDTO;
import de.x1c1b.taskcare.service.presentation.rest.v1.dto.UpdateBoardDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface BoardDTOMapper {

    BoardDTO mapToDTO(Board boardAggregate);

    @Mapping(source = "content", target = "content", defaultExpression = "java(new ArrayList<>())")
    PageDTO<BoardDTO> mapToDTO(Page<Board> boardAggregatePage);

    CreateBoardCommand mapToCommand(CreateBoardDTO createBoardDTO, String creator);

    UpdateBoardByIdCommand mapToCommand(UpdateBoardDTO updateBoardDTO, UUID id);
}
