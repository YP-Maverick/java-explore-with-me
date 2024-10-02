package ru.practicum.ewm.main.user.service;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.main.exception.ConflictException;
import ru.practicum.ewm.main.exception.NotFoundException;
import ru.practicum.ewm.main.user.dto.CreateUserDto;
import ru.practicum.ewm.main.user.dto.UserDto;
import ru.practicum.ewm.main.user.mapper.UserMapper;
import ru.practicum.ewm.main.user.model.User;
import ru.practicum.ewm.main.user.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;
    private final UserMapper userMapper;

    @Override
    public UserDto createUser(CreateUserDto createUserDto) {
        log.info("Request to create user.");

        try {
            User user = userStorage.save(userMapper.toModel(createUserDto));
            return userMapper.toDto(user);
        } catch (DataIntegrityViolationException e) {
            log.error("Duplicate. Attempt to create user with already occupied email address {}",
                    createUserDto.getEmail());
            throw new ConflictException(e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    @Override
    public UserDto getUserById(Long id) {
        log.info("Request to get user with id {}", id);

        User user = userStorage.findById(id).orElseThrow(() -> {
            log.error("NotFound. User with id {} does not exist.", id);
            return new NotFoundException(
                    String.format("User with id %d is not exist.", id)
            );
        });
        return userMapper.toDto(user);
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getUsers(List<Long> ids, Pageable pageable) {
        log.info("Request to get list of users with ids {}.", ids);

        List<User> users = (ids == null || ids.isEmpty())
                ? userStorage.findAll(pageable).getContent()
                : userStorage.findByIdIn(ids, pageable);
        return users.stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Long deleteUser(Long id) {
        log.info("Request to delete user with id {}", id);

        getUserById(id);

        userStorage.deleteById(id);
        return id;
    }
}
