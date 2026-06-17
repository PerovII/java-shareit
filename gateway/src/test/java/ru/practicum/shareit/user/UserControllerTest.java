package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserClient userClient;

    @Test
    void create_whenValid_thenReturnsOk() throws Exception {
        NewUserDto dto = new NewUserDto();
        dto.setName("User");
        dto.setEmail("user@mail.com");

        when(userClient.create(any())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(userClient).create(any());
    }

    @Test
    void getUser_whenValid_thenReturnsOk() throws Exception {
        long userId = 1L;
        when(userClient.getUser(userId)).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isOk());

        verify(userClient).getUser(userId);
    }

    @Test
    void update_whenValid_thenReturnsOk() throws Exception {
        long userId = 1L;
        UpdateUserDto dto = new UpdateUserDto();
        dto.setName("Updated");

        when(userClient.update(eq(userId), any())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(patch("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(userClient).update(eq(userId), any());
    }

    @Test
    void delete_whenValid_thenReturnsOk() throws Exception {
        long userId = 1L;
        when(userClient.deleteUser(userId)).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isOk());

        verify(userClient).deleteUser(userId);
    }
}