package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constant.HeaderConstants;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.service.ItemService;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getAllItems(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId) {
        log.info("Запрос списка предметов пользователя с id={}", userId);
        return itemService.getAll(userId);
    }

    @GetMapping("/{id}")
    public ItemDto getItem(@PathVariable long id) {
        log.info("Запрос информации о предмете id={}", id);
        return itemService.getItemById(id);
    }

    @GetMapping("/search")
    public List<ItemDto> getSearchItems(@RequestParam String text) {
        log.info("Запрос предмета: {}", text);
        return itemService.getSearchItems(text);
    }

    @PostMapping
    public ItemDto create(
            @RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId,
            @Valid @RequestBody NewItemDto request) {
        log.info("Запрос на создание предмета {}, пользователь id={}", request.getName(), userId);
        return itemService.create(userId, request);
    }

    @PatchMapping("/{id}")
    public ItemDto update(
            @RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId,
            @PathVariable long id,
            @Valid @RequestBody UpdateItemDto request) {
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


}
