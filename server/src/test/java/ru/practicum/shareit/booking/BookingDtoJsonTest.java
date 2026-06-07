package ru.practicum.shareit.booking;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookerInfoDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ItemInfoDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoJsonTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    @SneakyThrows
    void testBookingDto() {

        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(LocalDateTime.of(2026, 6, 10, 14, 0, 0));
        bookingDto.setEnd(LocalDateTime.of(2026, 6, 15, 14, 0, 0));
        bookingDto.setStatus("WAITING");
        BookerInfoDto booker = new BookerInfoDto();
        booker.setId(2L);
        booker.setName("Иван");
        bookingDto.setBooker(booker);

        ItemInfoDto item = new ItemInfoDto();
        item.setId(3L);
        item.setName("Дрель");
        bookingDto.setItem(item);

        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2026-06-10T14:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2026-06-15T14:00:00");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo("Иван");
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(3);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("Дрель");
    }
}