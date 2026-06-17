package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import java.util.List;

public interface ItemRequestService {

    ItemRequestDto create(long userId, NewItemRequestDto requestDto);

    List<ItemRequestDto> getUserRequests(long userId);

    List<ItemRequestDto> getAllRequests(long userId);

    ItemRequestDto getRequestById(long userId, long requestId);
}