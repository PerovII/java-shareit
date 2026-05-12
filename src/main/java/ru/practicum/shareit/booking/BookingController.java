package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.constant.HeaderConstants;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(
            @RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId,
            @Valid @RequestBody NewBookingDto request) {
        log.info("Запрос на создание бронирования предмета id={}, пользователь id={}", request.getItemId(), userId);
        return bookingService.create(userId, request);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto confirmBooking(
            @RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId,
            @PathVariable long bookingId,
            @RequestParam boolean approved) {
        log.info("Запрос на подтверждение бронирования id={}, статус подтверждения={}, пользователь id={}", bookingId, approved, userId);
        return bookingService.confirmBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(
            @RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId,
            @PathVariable long bookingId) {
        log.info("Запрос информации о бронировании id={}, пользователь id={}", bookingId, userId);
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getBookingWithState(
            @RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId,
            @RequestParam(defaultValue = "ALL") State state) {
        log.info("Запрос списка бронирований пользователя id={}, состояние={}", userId, state);
        return bookingService.getBookingsWithState(userId, state);

    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsForUserItems(
            @RequestHeader(HeaderConstants.X_SHARER_USER_ID) long userId,
            @RequestParam(defaultValue = "ALL") State state) {
        log.info("Запрос списка бронирований для вещей пользователя id={}, состояние={}", userId, state);
        return bookingService.getBookingsForUserItems(userId, state);
    }

}
