package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {

    List<ItemWithBookingDto> getAll(long userId);

    ItemWithBookingDto getItemById(long userId, long id);

    List<ItemDto> getSearchItems(String text);

    ItemDto create(long userId, NewItemDto request);

    void delete(long userId, long id);

    ItemDto update(long userId, long itemId, UpdateItemDto request);

    CommentDto createComment(long userId, long itemId, NewCommentDto request);

}
