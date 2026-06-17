package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;

import java.util.List;

public interface BookingService {

    BookingDto create(long userId, NewBookingDto request);

    BookingDto confirmBooking(long userId, long bookingId, boolean approved);

    BookingDto getBookingById(long userId, long bookingId);

    List<BookingDto> getBookingsWithState(long userId, BookingState bookingState);

    List<BookingDto> getBookingsForUserItems(long userId, BookingState bookingState);










}
