package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.BookingState;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User booker;
    private User owner;
    private Item item;
    private Booking booking;

    @BeforeEach
    void setUp() {
        booker = new User();
        booker.setId(1L);

        owner = new User();
        owner.setId(2L);

        item = new Item();
        item.setId(1L);
        item.setOwner(owner);
        item.setAvailable(true);

        booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
    }

    @Test
    void create_whenValid_thenReturnBookingDto() {
        NewBookingDto request = new NewBookingDto();
        request.setItemId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDto result = bookingService.create(1L, request);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void create_whenUserNotFound_thenThrowNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> bookingService.create(1L, new NewBookingDto()));
    }

    @Test
    void create_whenItemNotFound_thenThrowNotFoundException() {
        NewBookingDto request = new NewBookingDto();
        request.setItemId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.create(1L, request));
    }

    @Test
    void create_whenUserIsOwner_thenThrowUserValidationException() {
        NewBookingDto request = new NewBookingDto();
        request.setItemId(1L);

        when(userRepository.findById(2L)).thenReturn(Optional.of(owner)); // владелец пытается забронировать
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(UserValidationException.class, () -> bookingService.create(2L, request));
    }

    @Test
    void create_whenItemNotAvailable_thenThrowItemAvailabilityException() {
        NewBookingDto request = new NewBookingDto();
        request.setItemId(1L);
        item.setAvailable(false);

        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(ItemAvailabilityException.class, () -> bookingService.create(1L, request));
    }

    @Test
    void confirmBooking_whenApproved_thenReturnUpdatedBooking() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDto result = bookingService.confirmBooking(2L, 1L, true); // 2L - это owner.getId()

        assertEquals(BookingStatus.APPROVED.name(), result.getStatus());
    }

    @Test
    void confirmBooking_whenRejected_thenReturnUpdatedBooking() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDto result = bookingService.confirmBooking(2L, 1L, false);

        assertEquals(BookingStatus.REJECTED.name(), result.getStatus());
    }

    @Test
    void confirmBooking_whenUserIsNotOwner_thenThrowUserValidationException() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        // 1L - это booker.getId(), он не может подтверждать
        assertThrows(UserValidationException.class, () -> bookingService.confirmBooking(1L, 1L, true));
    }

    @Test
    void getBookingById_whenUserIsBooker_thenReturnBooking() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        BookingDto result = bookingService.getBookingById(1L, 1L); // 1L - booker

        assertNotNull(result);
    }

    @Test
    void getBookingById_whenUserIsOwner_thenReturnBooking() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        BookingDto result = bookingService.getBookingById(2L, 1L); // 2L - owner

        assertNotNull(result);
    }

    @Test
    void getBookingById_whenUserIsNotParticipant_thenThrowUserValidationException() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(UserValidationException.class, () -> bookingService.getBookingById(99L, 1L)); // 99L - левый юзер
    }

    @Test
    void getBookingsWithState_whenStateAll_thenReturnList() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(1L)).thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getBookingsWithState(1L, BookingState.ALL);

        assertEquals(1, result.size());
    }

    @Test
    void getBookingsWithState_whenStateCurrent_thenReturnList() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getBookingsWithState(1L, BookingState.CURRENT);

        assertEquals(1, result.size());
    }

    @Test
    void getBookingsWithState_whenStatePast_thenReturnList() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(eq(1L), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getBookingsWithState(1L, BookingState.PAST);

        assertEquals(1, result.size());
    }

    @Test
    void getBookingsWithState_whenStateFuture_thenReturnList() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(eq(1L), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getBookingsWithState(1L, BookingState.FUTURE);

        assertEquals(1, result.size());
    }

    @Test
    void getBookingsWithState_whenStateWaiting_thenReturnList() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(1L, BookingStatus.WAITING))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getBookingsWithState(1L, BookingState.WAITING);

        assertEquals(1, result.size());
    }

    @Test
    void getBookingsWithState_whenStateRejected_thenReturnList() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(1L, BookingStatus.REJECTED))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getBookingsWithState(1L, BookingState.REJECTED);

        assertEquals(1, result.size());
    }

    @Test
    void getBookingsForUserItems_whenUserHasNoItems_thenReturnEmptyList() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        when(itemRepository.findAllByOwnerId(1L)).thenReturn(Collections.emptyList());

        List<BookingDto> result = bookingService.getBookingsForUserItems(1L, BookingState.ALL);

        assertTrue(result.isEmpty());
    }

    @Test
    void getBookingsForUserItems_whenStateCurrent_thenReturnList() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerId(2L)).thenReturn(List.of(item));
        when(bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(eq(2L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getBookingsForUserItems(2L, BookingState.CURRENT);

        assertEquals(1, result.size());
    }

    @Test
    void getBookingsForUserItems_whenStatePast_thenReturnList() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerId(2L)).thenReturn(List.of(item));
        when(bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(eq(2L), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getBookingsForUserItems(2L, BookingState.PAST);

        assertEquals(1, result.size());
    }

    @Test
    void getBookingsForUserItems_whenStateFuture_thenReturnList() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerId(2L)).thenReturn(List.of(item));
        when(bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(eq(2L), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getBookingsForUserItems(2L, BookingState.FUTURE);

        assertEquals(1, result.size());
    }

    @Test
    void getBookingsForUserItems_whenStateWaiting_thenReturnList() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerId(2L)).thenReturn(List.of(item));
        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(2L, BookingStatus.WAITING))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getBookingsForUserItems(2L, BookingState.WAITING);

        assertEquals(1, result.size());
    }

    @Test
    void getBookingsForUserItems_whenStateRejected_thenReturnList() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerId(2L)).thenReturn(List.of(item));
        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(2L, BookingStatus.REJECTED))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getBookingsForUserItems(2L, BookingState.REJECTED);

        assertEquals(1, result.size());
    }
}