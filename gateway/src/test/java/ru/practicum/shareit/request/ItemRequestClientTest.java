package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@RestClientTest(ItemRequestClient.class)
class ItemRequestClientTest {

    @Autowired
    private ItemRequestClient itemRequestClient;

    @Autowired
    private MockRestServiceServer mockServer;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void create_whenValid_thenReturnsOk() throws Exception {
        long userId = 1L;
        NewItemRequestDto dto = new NewItemRequestDto();
        dto.setDescription("Нужен перфоратор");

        mockServer.expect(requestTo(containsString("/requests")))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                .andExpect(content().json(objectMapper.writeValueAsString(dto)))
                .andRespond(withStatus(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"id\": 1}"));

        ResponseEntity<Object> response = itemRequestClient.create(userId, dto);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        mockServer.verify();
    }

    @Test
    void getUserRequests_whenValid_thenReturnsOk() {
        long userId = 1L;

        mockServer.expect(requestTo(containsString("/requests")))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("[]"));

        ResponseEntity<Object> response = itemRequestClient.getUserRequests(userId);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        mockServer.verify();
    }

    @Test
    void getAllRequests_whenValid_thenReturnsOk() {
        long userId = 1L;

        mockServer.expect(requestTo(containsString("/requests/all")))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("[]"));

        ResponseEntity<Object> response = itemRequestClient.getAllRequests(userId);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        mockServer.verify();
    }

    @Test
    void getRequestById_whenValid_thenReturnsOk() {
        long userId = 1L;
        long requestId = 10L;

        mockServer.expect(requestTo(containsString("/requests/10")))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"id\": 10}"));

        ResponseEntity<Object> response = itemRequestClient.getRequestById(userId, requestId);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        mockServer.verify();
    }
}