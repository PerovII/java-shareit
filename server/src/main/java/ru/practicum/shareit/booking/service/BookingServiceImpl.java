package ru.practicum.shareit.booking.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.ItemAvailabilityException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UserValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Transactional
    public BookingDto create(long userId, NewBookingDto request) {
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new NotFoundException("Предмет не найден"));

        if (item.getOwner().getId().equals(userId)) {
            throw new UserValidationException("Пользователь является владельцем предмета");
        }

        if (!item.isAvailable()) {
            throw new ItemAvailabilityException("Предмет недоступен для бронирования");
        }

        Booking booking = BookingMapper.mapToBooking(request, item, booker);
        booking = bookingRepository.save(booking);

        log.info("Бронирование успешно создано, id={}", booking.getId());
        return BookingMapper.mapToBookingDto(booking);
    }

    @Transactional
    public BookingDto confirmBooking(long userId, long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new UserValidationException("Пользователь не является владельцем предмета");
        }

        if (approved) booking.setStatus(BookingStatus.APPROVED);
        else booking.setStatus(BookingStatus.REJECTED);

        bookingRepository.save(booking);
        log.info("Статус бронирования id={} успешно обновлен", booking.getId());
        return BookingMapper.mapToBookingDto(booking);
    }

    @Transactional(readOnly = true)
    public BookingDto getBookingById(long userId, long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        if (!booking.getItem().getOwner().getId().equals(userId) && !booking.getBooker().getId().equals(userId)) {
            throw new UserValidationException("Пользователь не является владельцем предмета");
        }

        return BookingMapper.mapToBookingDto(booking);
    }

    @Transactional(readOnly = true)
    public List<BookingDto> getBookingsWithState(long userId, BookingState bookingState) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = switch (bookingState) {
            case CURRENT ->
                    bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now);
            case PAST ->
                    bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, now);
            case FUTURE ->
                    bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, now);
            case WAITING ->
                    bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
            case REJECTED ->
                    bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
            default ->
                    bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
        };

        return bookings.stream()
                .map(BookingMapper::mapToBookingDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BookingDto> getBookingsForUserItems(long userId, BookingState bookingState) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        if (itemRepository.findAllByOwnerId(userId).isEmpty()) {
            return Collections.emptyList();
        }

        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = switch (bookingState) {
            case CURRENT ->
                    bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now);
            case PAST ->
                    bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, now);
            case FUTURE ->
                    bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(userId, now);
            case WAITING ->
                    bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
            case REJECTED ->
                    bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
            default ->
                    bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId);
        };

        return bookings.stream()
                .map(BookingMapper::mapToBookingDto)
                .toList();
    }



}
