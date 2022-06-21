package de.x1c1b.taskcare.service.infrastructure.persistence.jpa.entity.mapper;

import de.x1c1b.taskcare.service.core.common.domain.Page;
import de.x1c1b.taskcare.service.core.user.domain.User;
import de.x1c1b.taskcare.service.infrastructure.persistence.jpa.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserEntityMapper {

    UserEntity mapToEntity(User user);

    User mapToDomain(UserEntity entity);

    @Mapping(source = "number", target = "page")
    @Mapping(source = "size", target = "perPage")
    @Mapping(source = "content", target = "content", defaultExpression = "java(new ArrayList<>())")
    Page<User> mapToDomain(org.springframework.data.domain.Page<UserEntity> page);
}
