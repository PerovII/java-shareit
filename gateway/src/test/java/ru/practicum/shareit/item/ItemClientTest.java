package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import ru.practicum.shareit.item.dto.NewCommentDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import java.util.List;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@RestClientTest(ItemClient.class)
class ItemClientTest {

    @Autowired
    private ItemClient itemClient;

    @Autowired
    private MockRestServiceServer mockServer;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllItems_whenValid_thenReturnsOk() {
        long userId = 1L;

        mockServer.expect(requestTo(containsString("/items")))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("[]"));

        ResponseEntity<Object> response = itemClient.getAllItems(userId);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        mockServer.verify();
    }

    @Test
    void getItem_whenValid_thenReturnsOk() {
        long userId = 1L;
        long itemId = 1L;

        mockServer.expect(requestTo(containsString("/items/1")))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"id\": 1}"));

        ResponseEntity<Object> response = itemClient.getItem(userId, itemId);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        mockServer.verify();
    }

    @Test
    void getSearchItems_whenTextIsBlank_thenReturnsEmptyList() {
        ResponseEntity<Object> response = itemClient.getSearchItems("   ");

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(List.of(), response.getBody());
    }

    @Test
    void getSearchItems_whenTextIsValid_thenReturnsOk() {
        String text = "drill";

        mockServer.expect(requestTo(containsString("/items/search?text=" + text)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("[]"));

        ResponseEntity<Object> response = itemClient.getSearchItems(text);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        mockServer.verify();
    }

    @Test
    void create_whenValid_thenReturnsOk() throws Exception {
        long userId = 1L;
        NewItemDto requestDto = new NewItemDto();
        requestDto.setName("Дрель");

        mockServer.expect(requestTo(containsString("/items")))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                .andExpect(content().json(objectMapper.writeValueAsString(requestDto)))
                .andRespond(withStatus(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"id\": 1}"));

        ResponseEntity<Object> response = itemClient.create(userId, requestDto);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        mockServer.verify();
    }

    @Test
    void update_whenValid_thenReturnsOk() throws Exception {
        long userId = 1L;
        long itemId = 1L;
        UpdateItemDto requestDto = new UpdateItemDto();
        requestDto.setName("Новая дрель");

        mockServer.expect(requestTo(containsString("/items/1")))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                .andExpect(content().json(objectMapper.writeValueAsString(requestDto)))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"id\": 1}"));

        ResponseEntity<Object> response = itemClient.update(userId, itemId, requestDto);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        mockServer.verify();
    }

    @Test
    void deleteItem_whenValid_thenReturnsOk() {
        long userId = 1L;
        long itemId = 1L;

        mockServer.expect(requestTo(containsString("/items/1")))
                .andExpect(method(HttpMethod.DELETE))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                .andRespond(withStatus(HttpStatus.OK));

        ResponseEntity<Object> response = itemClient.deleteItem(userId, itemId);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        mockServer.verify();
    }

    @Test
    void createComment_whenValid_thenReturnsOk() throws Exception {
        long userId = 1L;
        long itemId = 1L;
        NewCommentDto requestDto = new NewCommentDto();
        requestDto.setText("Хорошая вещь");

        mockServer.expect(requestTo(containsString("/items/1/comment")))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-Sharer-User-Id", String.valueOf(userId)))
                .andExpect(content().json(objectMapper.writeValueAsString(requestDto)))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"id\": 1}"));

        ResponseEntity<Object> response = itemClient.createComment(userId, itemId, requestDto);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        mockServer.verify();
    }
}