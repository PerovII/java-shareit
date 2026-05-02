package ru.practicum.shareit.item.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UserValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserStorage;

import java.util.List;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    public ItemServiceImpl(ItemStorage itemStorage, UserStorage userStorage) {

        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
    }

    public List<ItemDto> getAll(long id) {
        log.info("Запрос на получение списка предметов пользователя id={} выполнен", id);
        return itemStorage
                .getAll(id)
                .stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    public ItemDto getItemById(long id) {
        Item item = itemStorage.getById(id).orElseThrow(() -> new NotFoundException("Предмет не найден"));
        log.info("Информация о предмете id={} получена", id);
        return ItemMapper.mapToItemDto(item);
    }

    public List<ItemDto> getSearchItems(String text) {

        log.info("Запрос предмета: {} выполнен", text);
        return itemStorage
                .getSearchItems(text)
                .stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    public ItemDto create(long userId, NewItemDto request) {
        Item item = ItemMapper.mapToItem(request);
        userStorage.getById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        item.setOwner(userId);
        item = itemStorage.create(item);

        log.info("Предмет успешно создан, id={}", item.getId());
        return ItemMapper.mapToItemDto(item);
    }

    public void delete(long userId, long id) {
        Item item = itemStorage.getById(id).orElseThrow(() -> new NotFoundException("Предмет не найден"));
        if (item.getOwner() != userId)
            throw new UserValidationException("Пользователь не является владельцем предмета");

        log.info("Предмет с id={} удален", id);
        itemStorage.delete(id);
    }

    public ItemDto update(long userId, long itemId, UpdateItemDto request) {
        Item item = itemStorage.getById(itemId).orElseThrow(() -> new NotFoundException("Предмет не найден"));
        if (item.getOwner() != userId)
            throw new UserValidationException("Пользователь не является владельцем предмета");
        Item updatedItem = ItemMapper.updateItemFields(item, request);

        log.info("Информация о предмете с id={} обновлена", itemId);
        itemStorage.update(updatedItem);

        return ItemMapper.mapToItemDto(updatedItem);
    }
}
