package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookerInfoDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ItemInfoDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {

    public static Booking mapToBooking(NewBookingDto dto, Item item, User booker) {
        Booking booking = new Booking();
        booking.setStart(dto.getStart());
        booking.setEnd(dto.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        return booking;
    }

    public static BookingDto mapToBookingDto(Booking booking) {
        BookingDto dto = new BookingDto();
        BookerInfoDto bookerInfo = new BookerInfoDto();
        ItemInfoDto itemInfo = new ItemInfoDto();
        bookerInfo.setId(booking.getBooker().getId());
        bookerInfo.setName(booking.getBooker().getName());
        itemInfo.setId(booking.getItem().getId());
        itemInfo.setName(booking.getItem().getName());
        dto.setId(booking.getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setStatus(booking.getStatus().toString());
        dto.setBooker(bookerInfo);
        dto.setItem(itemInfo);

        return dto;
    }
}
