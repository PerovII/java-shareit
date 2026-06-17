package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    private static final String HEADER = "X-Sharer-User-Id";

    @Test
    void create() throws Exception {
        NewItemRequestDto dto = new NewItemRequestDto();
        dto.setDescription("Desc");

        when(itemRequestService.create(anyLong(), any())).thenReturn(new ItemRequestDto());

        mockMvc.perform(post("/requests")
                        .header(HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void getUserRequests() throws Exception {
        when(itemRequestService.getUserRequests(anyLong())).thenReturn(List.of(new ItemRequestDto()));

        mockMvc.perform(get("/requests")
                        .header(HEADER, 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getAllRequests() throws Exception {
        when(itemRequestService.getAllRequests(anyLong())).thenReturn(List.of(new ItemRequestDto()));

        mockMvc.perform(get("/requests/all")
                        .header(HEADER, 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getRequestById() throws Exception {
        when(itemRequestService.getRequestById(anyLong(), anyLong())).thenReturn(new ItemRequestDto());

        mockMvc.perform(get("/requests/1")
                        .header(HEADER, 1L))
                .andExpect(status().isOk());
    }
}