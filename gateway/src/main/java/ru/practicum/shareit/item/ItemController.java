package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constant.HeaderConstants;
import ru.practicum.shareit.item.dto.NewCommentDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getAllItems(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId) {
        log.info("Запрос списка предметов пользователя с id={}", userId);
        return itemClient.getAllItems(userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItem(
            @RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId,
            @PathVariable long id) {
        log.info("Запрос информации о предмете id={}, пользователь id={}", id, userId);
        return itemClient.getItem(userId, id);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getSearchItems(@RequestParam String text) {
        log.info("Запрос предмета: {}", text);
        return itemClient.getSearchItems(text);
    }

    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId,
            @Valid @RequestBody NewItemDto request) {
        log.info("Запрос на создание предмета {}, пользователь id={}", request.getName(), userId);
        return itemClient.create(userId, request);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(
            @RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId,
            @PathVariable long id,
            @Valid @RequestBody UpdateItemDto request) {
        log.info("Запрос на изменение информации о предмете с id={}, пользователь id={}", id, userId);
        return itemClient.update(userId, id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteItem(
            @RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId,
            @PathVariable long id) {
        log.info("Запрос на удаление предмета с id={}, пользователь id={}", id, userId);
        return itemClient.deleteItem(userId, id);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(
            @RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId,
            @PathVariable long itemId,
            @Valid @RequestBody NewCommentDto request) {
        log.info("Запрос на добавление отзыва к предмету id={}, пользователь id={}", itemId, userId);
        return itemClient.createComment(userId, itemId, request);
    }
}