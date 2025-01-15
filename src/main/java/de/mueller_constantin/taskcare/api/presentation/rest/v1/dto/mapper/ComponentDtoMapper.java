package de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.mapper;

import de.mueller_constantin.taskcare.api.core.board.application.command.AddComponentByIdCommand;
import de.mueller_constantin.taskcare.api.core.board.application.command.UpdateComponentByIdCommand;
import de.mueller_constantin.taskcare.api.core.board.domain.ComponentProjection;
import de.mueller_constantin.taskcare.api.core.common.domain.Page;
import de.mueller_constantin.taskcare.api.presentation.rest.v1.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Optional;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ComponentDtoMapper {
    ComponentDto mapToDto(ComponentProjection componentProjection);

    @Mapping(source = "addComponentDto.description", target = "description", qualifiedByName = "unwrapOptional")
    AddComponentByIdCommand mapToCommand(UUID boardId, AddComponentDto addComponentDto);

    @Mapping(source = "updateComponentDto.name", target = "name", qualifiedByName = "unwrapOptional")
    @Mapping(source = "updateComponentDto.description", target = "description", qualifiedByName = "unwrapOptional")
    UpdateComponentByIdCommand mapToCommand(UUID boardId, UUID componentId, UpdateComponentDto updateComponentDto);

    @Mapping(source = "content", target = "content", defaultExpression = "java(new ArrayList<>())")
    PageDto<ComponentDto> mapToDto(Page<ComponentProjection> componentProjection);

                                @Named("unwrapOptional")
    default <T> T unwrapOptional(Optional<T> optional) {
        return optional.orElse(null);
    }
}
