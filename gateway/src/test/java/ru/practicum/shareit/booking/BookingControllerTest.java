package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.constant.HeaderConstants;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingClient bookingClient;

    @Test
    void bookItem_whenValid_thenReturnsOk() throws Exception {
        long userId = 1L;
        BookItemRequestDto dto = new BookItemRequestDto(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));

        when(bookingClient.bookItem(eq(userId), any())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/bookings")
                        .header(HeaderConstants.X_SHARER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(bookingClient).bookItem(eq(userId), any());
    }

    @Test
    void confirmBooking_whenValid_thenReturnsOk() throws Exception {
        long userId = 1L;
        long bookingId = 1L;

        when(bookingClient.confirmBooking(eq(userId), eq(bookingId), eq(true))).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header(HeaderConstants.X_SHARER_USER_ID, userId)
                        .param("approved", "true"))
                .andExpect(status().isOk());

        verify(bookingClient).confirmBooking(userId, bookingId, true);
    }

    @Test
    void getBooking_whenValid_thenReturnsOk() throws Exception {
        long userId = 1L;
        long bookingId = 1L;

        when(bookingClient.getBooking(userId, bookingId)).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header(HeaderConstants.X_SHARER_USER_ID, userId))
                .andExpect(status().isOk());

        verify(bookingClient).getBooking(userId, bookingId);
    }

    @Test
    void getBookings_whenValid_thenReturnsOk() throws Exception {
        long userId = 1L;

        when(bookingClient.getBookings(eq(userId), any(BookingState.class), eq(0), eq(10)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/bookings")
                        .header(HeaderConstants.X_SHARER_USER_ID, userId)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());

        verify(bookingClient).getBookings(eq(userId), any(BookingState.class), eq(0), eq(10));
    }

    @Test
    void getBookingsOwner_whenValid_thenReturnsOk() throws Exception {
        long userId = 1L;

        when(bookingClient.getBookingsOwner(eq(userId), any(BookingState.class), eq(0), eq(10)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/bookings/owner")
                        .header(HeaderConstants.X_SHARER_USER_ID, userId)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());

        verify(bookingClient).getBookingsOwner(eq(userId), any(BookingState.class), eq(0), eq(10));
    }
}