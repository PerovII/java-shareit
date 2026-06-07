package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplIntegrationTest {

    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;

    @Test
    void createBooking_whenValidData_thenBookingIsCreatedWithWaitingStatus() {

        NewUserDto ownerDto = new NewUserDto();
        ownerDto.setName("Владелец");
        ownerDto.setEmail("owner@test.com");
        UserDto owner = userService.create(ownerDto);

        NewItemDto itemDto = new NewItemDto();
        itemDto.setName("Перфоратор");
        itemDto.setDescription("Для ремонта");
        itemDto.setAvailable(true);
        var item = itemService.create(owner.getId(), itemDto);

        NewUserDto bookerDto = new NewUserDto();
        bookerDto.setName("Арендатор");
        bookerDto.setEmail("booker@test.com");
        UserDto booker = userService.create(bookerDto);

        NewBookingDto newBookingDto = new NewBookingDto();
        newBookingDto.setItemId(item.getId());
        newBookingDto.setStart(LocalDateTime.now().plusDays(1));
        newBookingDto.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto createdBooking = bookingService.create(booker.getId(), newBookingDto);

        assertThat(createdBooking).isNotNull();
        assertThat(createdBooking.getId()).isPositive();
        assertThat(createdBooking.getStatus()).isEqualTo("WAITING");
        assertThat(createdBooking.getItem().getId()).isEqualTo(item.getId());
        assertThat(createdBooking.getItem().getName()).isEqualTo("Перфоратор");
        assertThat(createdBooking.getBooker().getId()).isEqualTo(booker.getId());
    }
}