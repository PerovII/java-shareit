package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestDto create(long userId, NewItemRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        ItemRequest request = new ItemRequest();
        request.setDescription(requestDto.getDescription());
        request.setRequestor(user);
        request.setCreated(LocalDateTime.now());

        request = itemRequestRepository.save(request);
        return ItemRequestMapper.mapToDto(request, Collections.emptyList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getUserRequests(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }
        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(userId);
        return mapRequestsToDto(requests);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getAllRequests(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }
        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorIdNotOrderByCreatedDesc(userId);
        return mapRequestsToDto(requests);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestDto getRequestById(long userId, long requestId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }
        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос не найден"));

        List<Item> items = itemRepository.findAllByRequestId(requestId);
        return ItemRequestMapper.mapToDto(request, items);
    }

    private List<ItemRequestDto> mapRequestsToDto(List<ItemRequest> requests) {
        if (requests.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> requestIds = requests.stream().map(ItemRequest::getId).toList();
        Map<Long, List<Item>> itemsByRequest = itemRepository.findAllByRequestIdIn(requestIds).stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId()));

        return requests.stream()
                .map(request -> ItemRequestMapper.mapToDto(
                        request,
                        itemsByRequest.getOrDefault(request.getId(), Collections.emptyList())
                ))
                .toList();
    }
}