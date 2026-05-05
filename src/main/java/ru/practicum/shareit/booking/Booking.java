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

   private Long id;
   private LocalDateTime start;
   private LocalDateTime end;
   private Long booker;
   private BookingStatus status;

}

