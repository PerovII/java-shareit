package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.NewItemRequestDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestClient itemRequestClient;

    private static final String HEADER = "X-Sharer-User-Id";

    @Test
    void create_whenValid_thenReturnsOk() throws Exception {
        long userId = 1L;
        NewItemRequestDto dto = new NewItemRequestDto();
        dto.setDescription("Description");

        when(itemRequestClient.create(eq(userId), any())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/requests")
                        .header(HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(itemRequestClient).create(eq(userId), any());
    }

    @Test
    void getUserRequests_whenValid_thenReturnsOk() throws Exception {
        long userId = 1L;

        when(itemRequestClient.getUserRequests(userId)).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/requests")
                        .header(HEADER, userId))
                .andExpect(status().isOk());

        verify(itemRequestClient).getUserRequests(userId);
    }

    @Test
    void getAllRequests_whenValid_thenReturnsOk() throws Exception {
        long userId = 1L;

        when(itemRequestClient.getAllRequests(userId)).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/requests/all")
                        .header(HEADER, userId))
                .andExpect(status().isOk());

        verify(itemRequestClient).getAllRequests(userId);
    }

    @Test
    void getRequestById_whenValid_thenReturnsOk() throws Exception {
        long userId = 1L;
        long requestId = 1L;

        when(itemRequestClient.getRequestById(userId, requestId)).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header(HEADER, userId))
                .andExpect(status().isOk());

        verify(itemRequestClient).getRequestById(userId, requestId);
    }
}