package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import java.util.List;

public interface ItemService {

    List<ItemDto> getAll(long id);

    ItemDto getItemById(long id);

    List<ItemDto> getSearchItems(String text);

    ItemDto create(long userId, NewItemDto request);

    void delete(long userId, long id);

    ItemDto update(long userId, long itemId, UpdateItemDto request);

}
