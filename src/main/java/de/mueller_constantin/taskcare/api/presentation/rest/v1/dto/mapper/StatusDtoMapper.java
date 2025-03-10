package de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.mapper;

import de.mueller_constantin.taskcare.api.core.board.domain.StatusCategory;
import de.mueller_constantin.taskcare.api.core.common.domain.Page;
import de.mueller_constantin.taskcare.api.core.board.application.command.AddStatusByIdCommand;
import de.mueller_constantin.taskcare.api.core.board.application.command.UpdateStatusByIdCommand;
import de.mueller_constantin.taskcare.api.core.board.domain.StatusProjection;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.AddStatusDto;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.PageDto;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.StatusDto;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.UpdateStatusDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Optional;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface StatusDtoMapper {
    StatusDto mapToDto(StatusProjection statusProjection);

    @Mapping(source = "addStatusDto.description", target = "description", qualifiedByName = "unwrapOptional")
    AddStatusByIdCommand mapToCommand(UUID boardId, AddStatusDto addStatusDto);

    @Mapping(source = "updateStatusDto.name", target = "name", qualifiedByName = "unwrapOptional")
    @Mapping(source = "updateStatusDto.description", target = "description", qualifiedByName = "unwrapOptional")
    @Mapping(source = "updateStatusDto.category", target = "category", qualifiedByName = "unwrapOptional")
    UpdateStatusByIdCommand mapToCommand(UUID boardId, UUID statusId, UpdateStatusDto updateStatusDto);

    @Mapping(source = "content", target = "content", defaultExpression = "java(new ArrayList<>())")
    PageDto<StatusDto> mapToDto(Page<StatusProjection> statusProjectionPage);

                                @Named("unwrapOptional")
    default <T> T unwrapOptional(Optional<T> optional) {
        return optional.orElse(null);
    }

    default StatusCategory mapCategory(String category) {
        if(category != null) {
            return StatusCategory.valueOf(category);
        } else {
            return null;
        }
    }
}
