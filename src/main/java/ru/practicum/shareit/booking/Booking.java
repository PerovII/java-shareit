package ru.practicum.shareit.booking;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Getter
@Setter
@NoArgsConstructor
public class Booking {

    Long id;
    LocalDateTime start;
    LocalDateTime end;
    Long booker;
    BookingStatus status;

}

