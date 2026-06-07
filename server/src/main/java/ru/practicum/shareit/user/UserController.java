package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable long id) {
        log.info("Запрос информации о пользователе с id={}", id);
        return userService.getUserById(id);
    }

    @PostMapping
    public UserDto create(@Valid @RequestBody NewUserDto request) {
        log.info("Запрос на создание пользователя name={}", request.getName());
        return userService.create(request);
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable long id, @Valid @RequestBody UpdateUserDto request) {
        log.info("Запрос на изменение информации о пользователе с id={}", id);
        return userService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable long id) {
        log.info("Запрос на удаление пользователя с id={}", id);
        userService.delete(id);
    }


}
