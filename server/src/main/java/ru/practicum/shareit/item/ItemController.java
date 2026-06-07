package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constant.HeaderConstants;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public List<ItemWithBookingDto> getAllItems(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId) {
        log.info("Запрос списка предметов пользователя с id={}", userId);
        return itemService.getAll(userId);
    }

    @GetMapping("/{id}")
    public ItemWithBookingDto getItem(
            @RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId,
            @PathVariable long id) {
        log.info("Запрос информации о предмете id={}, пользователь id={}", id, userId);
        return itemService.getItemById(userId, id);
    }

    @GetMapping("/search")
    public List<ItemDto> getSearchItems(@RequestParam String text) {
        log.info("Запрос предмета: {}", text);
        return itemService.getSearchItems(text);
    }

    @PostMapping
    public ItemDto create(
            @RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId,
            @RequestBody NewItemDto request) {
        log.info("Запрос на создание предмета {}, пользователь id={}", request.getName(), userId);
        return itemService.create(userId, request);
    }

    @PatchMapping("/{id}")
    public ItemDto update(
            @RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId,
            @PathVariable long id,
            @RequestBody UpdateItemDto request) {
        log.info("Запрос на изменение информации о предмете с id={}, пользователь id={}", id, userId);
        return itemService.update(userId, id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteItem(
            @RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId,
            @PathVariable long id) {
        log.info("Запрос на удаление предмета с id={}, пользователь id={}", id, userId);
        itemService.delete(userId, id);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(
            @RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId,
            @PathVariable long itemId,
            @RequestBody NewCommentDto request) {
        log.info("Запрос на добавление отзыва к предмету id={}, пользователь id={}", itemId, userId);
        return itemService.createComment(userId, itemId, request);
    }



}
