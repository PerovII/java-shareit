package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserStorage;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public UserDto getUserById(long id) {
        User user = userStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        log.info("Пользователь с id={} успешно найден", id);
        return UserMapper.mapToUserDto(user);
    }

    public UserDto create(NewUserDto request) {
        User user = UserMapper.mapToUser(request);
        if (userStorage.existsByEmail(user.getEmail()))
            throw new EmailExistsException("Указанная электронная почта уже используется");
        user = userStorage.create(user);

        log.info("Пользователь успешно создан, id={}", user.getId());
        return UserMapper.mapToUserDto(user);
    }

    public void delete(long id) {
        userStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        log.info("Пользователь с id={} удален", id);
        userStorage.delete(id);
    }

    public UserDto update(long id, UpdateUserDto request) {
        User user = userStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        if (request.hasEmail() && !user.getEmail().equalsIgnoreCase(request.getEmail())) {
            if (userStorage.existsByEmail(request.getEmail())) {
                throw new EmailExistsException("Указанная электронная почта уже используется");
            }
        }

        User updatedUser = UserMapper.updateUserFields(user, request);
        userStorage.update(updatedUser);

        log.info("Информация о пользователе с id={} обновлена", id);
        return UserMapper.mapToUserDto(updatedUser);
    }

}
