package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemReplyDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemRequestMapper {

    public static ItemRequestDto mapToDto(ItemRequest request, List<Item> items) {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(request.getId());
        dto.setDescription(request.getDescription());
        dto.setCreated(request.getCreated());

        if (items != null) {
            dto.setItems(items.stream()
                    .map(item -> new ItemReplyDto(item.getId(), item.getName(), item.getOwner().getId()))
                    .toList());
        } else {
            dto.setItems(List.of());
        }
        return dto;
    }
}