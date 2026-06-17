package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserClient userClient;

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@PathVariable long id) {
        log.info("Запрос информации о пользователе с id={}", id);
        return userClient.getUser(id);
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody NewUserDto request) {
        log.info("Запрос на создание пользователя name={}", request.getName());
        return userClient.create(request);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable long id, @Valid @RequestBody UpdateUserDto request) {
        log.info("Запрос на изменение информации о пользователе с id={}", id);
        return userClient.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable long id) {
        log.info("Запрос на удаление пользователя с id={}", id);
        return userClient.deleteUser(id);
    }
}