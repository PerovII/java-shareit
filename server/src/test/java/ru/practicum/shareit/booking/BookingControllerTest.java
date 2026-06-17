package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
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
    private BookingService bookingService;

    private static final String HEADER = "X-Sharer-User-Id";

    @Test
    void create() throws Exception {
        NewBookingDto dto = new NewBookingDto();
        dto.setItemId(1L);
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));

        when(bookingService.create(anyLong(), any())).thenReturn(new BookingDto());

        mockMvc.perform(post("/bookings")
                        .header(HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void confirmBooking() throws Exception {
        when(bookingService.confirmBooking(anyLong(), anyLong(), anyBoolean())).thenReturn(new BookingDto());

        mockMvc.perform(patch("/bookings/1")
                        .header(HEADER, 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk());
    }

    @Test
    void getBooking() throws Exception {
        when(bookingService.getBookingById(anyLong(), anyLong())).thenReturn(new BookingDto());

        mockMvc.perform(get("/bookings/1")
                        .header(HEADER, 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getBookings() throws Exception {
        when(bookingService.getBookingsWithState(anyLong(), any())).thenReturn(List.of(new BookingDto()));

        mockMvc.perform(get("/bookings")
                        .header(HEADER, 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    void getBookingsOwner() throws Exception {
        when(bookingService.getBookingsForUserItems(anyLong(), any())).thenReturn(List.of(new BookingDto()));

        mockMvc.perform(get("/bookings/owner")
                        .header(HEADER, 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }
}