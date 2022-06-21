package de.x1c1b.taskcare.service.infrastructure.persistence.jpa;

import de.x1c1b.taskcare.service.core.common.domain.Page;
import de.x1c1b.taskcare.service.core.common.domain.PageSettings;
import de.x1c1b.taskcare.service.core.user.domain.User;
import de.x1c1b.taskcare.service.core.user.domain.UserRepository;
import de.x1c1b.taskcare.service.infrastructure.persistence.jpa.mapper.UserEntityMapper;
import de.x1c1b.taskcare.service.infrastructure.persistence.jpa.repository.UserEntityRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@AllArgsConstructor
public class JpaUserRepository implements UserRepository {

    private final UserEntityRepository userEntityRepository;
    private final UserEntityMapper userEntityMapper;

    @Override
    public Optional<User> findById(String username) {
        return userEntityRepository.findById(username)
                .map(userEntityMapper::mapToDomain);
    }

    @Override
    public Page<User> findAll(PageSettings pageSettings) {
        var sortRequest = Sort.by(
                PageSettings.SortDirection.DESC.equals(pageSettings.getSortDirection()) ?
                        Sort.Direction.DESC :
                        Sort.Direction.ASC,
                null != pageSettings.getSortBy() ?
                        pageSettings.getSortBy() :
                        "username");

        var pageRequest = PageRequest.of(pageSettings.getPage(), pageSettings.getPerPage(), sortRequest);

        return userEntityMapper.mapToDomain(userEntityRepository.findAll(pageRequest));
    }

    @Override
    public boolean existsById(String username) {
        return userEntityRepository.existsById(username);
    }

    @Override
    @Transactional
    public boolean deleteById(String username) {
        if (existsById(username)) {
            userEntityRepository.deleteById(username);
            return true;
        }

        return false;
    }

    @Override
    public void save(User model) {
        userEntityRepository.save(userEntityMapper.mapToEntity(model));
    }

    @Override
    public boolean existsByEmail(String email) {
        return userEntityRepository.existsByEmail(email);
    }
}
