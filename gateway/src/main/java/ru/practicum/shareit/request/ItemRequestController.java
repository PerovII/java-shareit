package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constant.HeaderConstants;
import ru.practicum.shareit.request.dto.NewItemRequestDto;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/requests")
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId,
            @Valid @RequestBody NewItemRequestDto requestDto) {
        log.info("Создание запроса вещи пользователем id={}", userId);
        return itemRequestClient.create(userId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getUserRequests(
            @RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId) {
        log.info("Получение списка запросов пользователя id={}", userId);
        return itemRequestClient.getUserRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(
            @RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId) {
        log.info("Получение списка всех запросов, пользователь id={}", userId);
        return itemRequestClient.getAllRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(
            @RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId,
            @PathVariable long requestId) {
        log.info("Получение информации о запросе id={}, пользователь id={}", requestId, userId);
        return itemRequestClient.getRequestById(userId, requestId);
    }
}