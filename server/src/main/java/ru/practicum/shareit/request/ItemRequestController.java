package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constant.HeaderConstants;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto create(
            @RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId,
            @Valid @RequestBody NewItemRequestDto requestDto) {
        return itemRequestService.create(userId, requestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getUserRequests(
            @RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId) {
        return itemRequestService.getUserRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(
            @RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId) {
        return itemRequestService.getAllRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(
            @RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId,
            @PathVariable long requestId) {
        return itemRequestService.getRequestById(userId, requestId);
    }
}