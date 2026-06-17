package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@RestClientTest(BookingClient.class)
class BookingClientTest {

    @Autowired
    private BookingClient bookingClient;

    @Autowired
    private MockRestServiceServer mockServer;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getBookings_whenValid_thenReturnsOk() {
        long userId = 1L;
        BookingState state = BookingState.ALL;
        Integer from = 0;
        Integer size = 10;

        mockServer.expect(requestTo(containsString("/bookings?state=ALL&from=0&size=10")))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("[{\"id\": 1}]"));

        ResponseEntity<Object> response = bookingClient.getBookings(userId, state, from, size);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        mockServer.verify();
    }

    @Test
    void bookItem_whenValid_thenReturnsOk() throws Exception {
        long userId = 1L;
        BookItemRequestDto requestDto = new BookItemRequestDto(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));

        mockServer.expect(requestTo(containsString("/bookings")))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                .andExpect(content().json(objectMapper.writeValueAsString(requestDto)))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"id\": 1}"));

        ResponseEntity<Object> response = bookingClient.bookItem(userId, requestDto);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        mockServer.verify();
    }

    @Test
    void confirmBooking_whenValid_thenReturnsOk() {
        long userId = 1L;
        long bookingId = 1L;
        boolean approved = true;

        mockServer.expect(requestTo(containsString("/bookings/1?approved=true")))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"id\": 1, \"status\": \"APPROVED\"}"));

        ResponseEntity<Object> response = bookingClient.confirmBooking(userId, bookingId, approved);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        mockServer.verify();
    }
}