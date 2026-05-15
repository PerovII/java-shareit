package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;

import java.util.List;

public interface BookingService {

    BookingDto create(long userId, NewBookingDto request);

    BookingDto confirmBooking(long userId, long bookingId, boolean approved);

    BookingDto getBookingById(long userId, long bookingId);

    List<BookingDto> getBookingsWithState(long userId, State bookingState);

    List<BookingDto> getBookingsForUserItems(long userId, State bookingState);










}
