package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.CommentValidationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UserValidationException;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Transactional(readOnly = true)
    public List<ItemWithBookingDto> getAll(long userId) {
        List<Item> items = itemRepository.findAllByOwnerId(userId);

        if (items.isEmpty()) {
            return Collections.emptyList();
        }

        LocalDateTime now = LocalDateTime.now();

        List<Booking> pastBookings = bookingRepository
                .findByItemInAndStatusAndStartLessThanEqualOrderByStartDesc(items, BookingStatus.APPROVED, now);
        List<Booking> futureBookings = bookingRepository
                .findByItemInAndStatusAndStartGreaterThanOrderByStartAsc(items, BookingStatus.APPROVED, now);

        Map<Item, Booking> lastBookings = pastBookings.stream()
                .collect(Collectors.toMap(Booking::getItem, b -> b, (existing, replacement) -> existing));

        Map<Item, Booking> nextBookings = futureBookings.stream()
                .collect(Collectors.toMap(Booking::getItem, b -> b, (existing, replacement) -> existing));

        Map<Item, List<Comment>> commentsByItem = commentRepository.findAllByItemIn(items).stream()
                .collect(Collectors.groupingBy(Comment::getItem));

        log.info("Запрос списка предметов для пользователя id={} выполнен", userId);
        return items.stream()
                .map(item -> ItemMapper.mapToItemWithBookingDto(
                        item,
                        lastBookings.get(item),
                        nextBookings.get(item),
                        commentsByItem.getOrDefault(item, Collections.emptyList())
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public ItemWithBookingDto getItemById(long userId, long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Предмет не найден"));

        List<Comment> comments = commentRepository.findAllByItemId(id);

        Booking lastBooking = null;
        Booking nextBooking = null;

        if (item.getOwner().getId().equals(userId)) {
            LocalDateTime now = LocalDateTime.now();
            lastBooking = bookingRepository
                    .findFirstByItemIdAndStatusAndStartLessThanEqualOrderByStartDesc(id, BookingStatus.APPROVED, now);
            nextBooking = bookingRepository
                    .findFirstByItemIdAndStatusAndStartGreaterThanOrderByStartAsc(id, BookingStatus.APPROVED, now);
        }

        log.info("Запрос предмета id={} выполнен", item.getId());
        return ItemMapper.mapToItemWithBookingDto(item, lastBooking, nextBooking, comments);
    }

    @Transactional(readOnly = true)
    public List<ItemDto> getSearchItems(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        log.info("Запрос предмета: {} выполнен", text);
        return itemRepository.search(text).stream()
                .map(item -> ItemMapper.mapToItemDto(item, Collections.emptyList()))
                .toList();
    }

    @Transactional
    public ItemDto create(long userId, NewItemDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Item item = ItemMapper.mapToItem(request);
        item.setOwner(user);
        item = itemRepository.save(item);

        log.info("Предмет успешно создан, id={}", item.getId());
        return ItemMapper.mapToItemDto(item, Collections.emptyList());
    }

    @Transactional
    public void delete(long userId, long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Предмет не найден"));
        if (!item.getOwner().getId().equals(userId)) {
            throw new UserValidationException("Пользователь не является владельцем предмета");
        }

        log.info("Предмет с id={} удален", id);
        itemRepository.deleteById(id);
    }

    @Transactional
    public ItemDto update(long userId, long itemId, UpdateItemDto request) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет не найден"));
        if (!item.getOwner().getId().equals(userId)) {
            throw new UserValidationException("Пользователь не является владельцем предмета");
        }

        Item updatedItem = ItemMapper.updateItemFields(item, request);
        itemRepository.save(updatedItem);

        log.info("Информация о предмете с id={} обновлена", itemId);
        return ItemMapper.mapToItemDto(updatedItem, Collections.emptyList());
    }

    @Transactional
    public CommentDto createComment(long userId, long itemId, NewCommentDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет не найден"));

        if (!bookingRepository.existsByBookerIdAndItemIdAndStatusAndEndBefore(
                userId, itemId, BookingStatus.APPROVED, LocalDateTime.now())) {
            throw new CommentValidationException("Пользователь не брал предмет в аренду или аренда еще не завершена");
        }

        Comment comment = new Comment();
        comment.setText(request.getText());
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
        log.info("Комментарий к предмету {} добавлен", comment.getItem().getName());
        return ItemMapper.mapToCommentDto(commentRepository.save(comment));
    }
}