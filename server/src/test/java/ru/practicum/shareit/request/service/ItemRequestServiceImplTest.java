package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Test
    void create_whenUserFound_thenReturnItemRequestDto() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);

        NewItemRequestDto dto = new NewItemRequestDto();
        dto.setDescription("Нужна дрель");

        ItemRequest savedRequest = new ItemRequest();
        savedRequest.setId(1L);
        savedRequest.setDescription("Нужна дрель");
        savedRequest.setRequestor(user);
        savedRequest.setCreated(LocalDateTime.now());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(savedRequest);

        ItemRequestDto result = itemRequestService.create(userId, dto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Нужна дрель", result.getDescription());
    }

    @Test
    void create_whenUserNotFound_thenThrowNotFoundException() {
        long userId = 1L;
        NewItemRequestDto dto = new NewItemRequestDto();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.create(userId, dto));
    }

    @Test
    void getUserRequests_whenValid_thenReturnList() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);

        ItemRequest request = new ItemRequest();
        request.setId(1L);
        request.setRequestor(user);

        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(userId))
                .thenReturn(List.of(request));
        when(itemRepository.findAllByRequestIdIn(anyList())).thenReturn(Collections.emptyList());

        List<ItemRequestDto> result = itemRequestService.getUserRequests(userId);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    void getUserRequests_whenUserNotFound_thenThrowNotFoundException() {
        long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> itemRequestService.getUserRequests(userId));
    }

    @Test
    void getAllRequests_whenValid_thenReturnList() {
        long userId = 1L;
        ItemRequest request = new ItemRequest();
        request.setId(2L);

        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRequestRepository.findAllByRequestorIdNotOrderByCreatedDesc(userId))
                .thenReturn(List.of(request));
        when(itemRepository.findAllByRequestIdIn(anyList())).thenReturn(Collections.emptyList());

        List<ItemRequestDto> result = itemRequestService.getAllRequests(userId);

        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getId());
    }

    @Test
    void getRequestById_whenValid_thenReturnItemRequestDto() {
        long userId = 1L;
        long requestId = 1L;

        ItemRequest request = new ItemRequest();
        request.setId(requestId);

        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(itemRepository.findAllByRequestId(requestId)).thenReturn(Collections.emptyList());

        ItemRequestDto result = itemRequestService.getRequestById(userId, requestId);

        assertNotNull(result);
        assertEquals(requestId, result.getId());
    }

    @Test
    void getRequestById_whenRequestNotFound_thenThrowNotFoundException() {
        long userId = 1L;
        long requestId = 1L;

        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.getRequestById(userId, requestId));
    }
}