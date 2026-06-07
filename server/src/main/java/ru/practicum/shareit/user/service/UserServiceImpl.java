package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EmailExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserDto getUserById(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        log.info("Пользователь с id={} успешно найден", id);
        return UserMapper.mapToUserDto(user);
    }

    @Transactional
    public UserDto create(NewUserDto request) {
        try {
            User user = UserMapper.mapToUser(request);
            user = userRepository.save(user);
            log.info("Пользователь успешно создан, id={}", user.getId());
            return UserMapper.mapToUserDto(user);
        } catch (DataIntegrityViolationException e) {
            throw new EmailExistsException("Указанная электронная почта уже используется");
        }
    }

    @Transactional
    public void delete(long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("Пользователь не найден");
        }
        log.info("Пользователь с id={} удален", id);
        userRepository.deleteById(id);
    }

    @Transactional
    public UserDto update(long id, UpdateUserDto request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        User updatedUser = UserMapper.updateUserFields(user, request);
        try {
            userRepository.save(updatedUser);
            log.info("Информация о пользователе с id={} обновлена", id);
            return UserMapper.mapToUserDto(updatedUser);
        } catch (DataIntegrityViolationException e) {
            throw new EmailExistsException("Указанная электронная почта уже используется");
        }
    }
}