package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ItemMapper {

    public static Item mapToItem(NewItemDto request) {
        Item item = new Item();
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setAvailable(request.getAvailable());
        return item;
    }

    public static ItemDto mapToItemDto(Item item, List<Comment> comments) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.isAvailable());
        dto.setOwnerId(item.getOwner().getId());
        if (item.getRequest() != null) {
            dto.setRequestId(item.getRequest().getId());
        }
        dto.setComments(comments != null ? comments.stream().map(ItemMapper::mapToCommentDto).toList() : List.of());
        return dto;
    }

    public static Item updateItemFields(Item item, UpdateItemDto request) {
        if (request.hasName()) item.setName(request.getName());
        if (request.hasDescription()) item.setDescription(request.getDescription());
        if (request.hasAvailable()) item.setAvailable(request.getAvailable());

        return item;
    }

    public static ItemWithBookingDto mapToItemWithBookingDto(Item item, Booking lastBooking, Booking nextBooking, List<Comment> comments) {
        ItemWithBookingDto dto = new ItemWithBookingDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.isAvailable());
        dto.setOwnerId(item.getOwner().getId());

        if (item.getRequest() != null) {
            dto.setRequestId(item.getRequest().getId());
        }
        if (lastBooking != null) {
            dto.setLastBooking(lastBooking.getStart());
        }
        if (nextBooking != null) {
            dto.setNextBooking(nextBooking.getStart());
        }
        dto.setComments(comments != null ? comments.stream().map(ItemMapper::mapToCommentDto).toList() : List.of());
        return dto;
    }

    public static CommentDto mapToCommentDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setText(comment.getText());
        dto.setAuthorName(comment.getAuthor().getName());
        dto.setCreated(comment.getCreated());
        return dto;
    }

}
