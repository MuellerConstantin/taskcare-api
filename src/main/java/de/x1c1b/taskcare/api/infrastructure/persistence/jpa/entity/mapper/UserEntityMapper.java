package de.x1c1b.taskcare.api.infrastructure.persistence.jpa.entity.mapper;

import de.x1c1b.taskcare.api.core.common.domain.Page;
import de.x1c1b.taskcare.api.core.user.domain.User;
import de.x1c1b.taskcare.api.infrastructure.persistence.jpa.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserEntityMapper {

    @Mapping(target = "memberships", ignore = true)
    UserEntity mapToEntity(User userAggregate);

    User mapToDomain(UserEntity userEntity);

    @Mapping(source = "number", target = "info.page")
    @Mapping(source = "size", target = "info.perPage")
    @Mapping(source = "totalElements", target = "info.totalElements")
    @Mapping(source = "totalPages", target = "info.totalPages")
    @Mapping(source = "content", target = "content", defaultExpression = "java(new ArrayList<>())")
    Page<User> mapToDomain(org.springframework.data.domain.Page<UserEntity> page);
}
