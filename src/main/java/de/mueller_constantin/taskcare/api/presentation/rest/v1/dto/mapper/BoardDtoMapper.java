package de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.mapper;

import de.mueller_constantin.taskcare.api.core.common.domain.Page;
import de.mueller_constantin.taskcare.api.core.board.application.command.CreateBoardCommand;
import de.mueller_constantin.taskcare.api.core.board.application.command.UpdateBoardByIdCommand;
import de.mueller_constantin.taskcare.api.core.board.domain.BoardProjection;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.BoardDto;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.CreateBoardDto;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.PageDto;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.UpdateBoardDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Optional;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface BoardDtoMapper {
    BoardDto mapToDto(BoardProjection boardProjection);

    @Mapping(source = "createBoardDto.description", target = "description", qualifiedByName = "unwrapOptional")
    CreateBoardCommand mapToCommand(CreateBoardDto createBoardDto, UUID creatorId);

    @Mapping(source = "updateBoardDto.name", target = "name", qualifiedByName = "unwrapOptional")
    @Mapping(source = "updateBoardDto.description", target = "description", qualifiedByName = "unwrapOptional")
    UpdateBoardByIdCommand mapToCommand(UUID id, UpdateBoardDto updateBoardDto);

    @Mapping(source = "content", target = "content", defaultExpression = "java(new ArrayList<>())")
    PageDto<BoardDto> mapToDto(Page<BoardProjection> boardProjectionPage);

    @Named("unwrapOptional")
    default <T> T unwrapOptional(Optional<T> optional) {
        return optional.orElse(null);
    }
}
