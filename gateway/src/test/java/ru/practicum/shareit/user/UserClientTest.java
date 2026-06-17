package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@RestClientTest(UserClient.class)
class UserClientTest {

    @Autowired
    private UserClient userClient;

    @Autowired
    private MockRestServiceServer mockServer;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createUser_whenValid_thenReturnsOk() throws Exception {
        NewUserDto requestDto = new NewUserDto();
        requestDto.setName("User");
        requestDto.setEmail("user@mail.com");

        mockServer.expect(requestTo(containsString("/users")))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json(objectMapper.writeValueAsString(requestDto)))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"id\": 1, \"name\": \"User\", \"email\": \"user@mail.com\"}"));

        ResponseEntity<Object> response = userClient.create(requestDto);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        mockServer.verify();
    }

    @Test
    void getUser_whenValid_thenReturnsOk() {
        mockServer.expect(requestTo(containsString("/users/1")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"id\": 1}"));

        ResponseEntity<Object> response = userClient.getUser(1L);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        mockServer.verify();
    }

    @Test
    void update_whenValid_thenReturnsOk() throws Exception {
        long userId = 1L;
        UpdateUserDto requestDto = new UpdateUserDto();
        requestDto.setName("UpdatedName");

        mockServer.expect(requestTo(containsString("/users/" + userId)))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(content().json(objectMapper.writeValueAsString(requestDto)))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"id\": 1, \"name\": \"UpdatedName\", \"email\": \"user@mail.com\"}"));

        ResponseEntity<Object> response = userClient.update(userId, requestDto);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        mockServer.verify();
    }

    @Test
    void deleteUser_whenValid_thenReturnsOk() {
        long userId = 1L;

        mockServer.expect(requestTo(containsString("/users/" + userId)))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.OK));

        ResponseEntity<Object> response = userClient.deleteUser(userId);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        mockServer.verify();
    }
}