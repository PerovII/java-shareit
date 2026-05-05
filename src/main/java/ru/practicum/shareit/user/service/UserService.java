package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {

    UserDto getUserById(long id);

    UserDto create(NewUserDto request);

    UserDto update(long id, UpdateUserDto request);

    void delete(long id);
}
