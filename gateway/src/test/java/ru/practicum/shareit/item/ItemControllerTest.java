package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.NewItemDto;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemClient itemClient;

    @Test
    @SneakyThrows
    void createItem_whenItemIsValid_thenReturnsStatusOk() {
        NewItemDto newItemDto = new NewItemDto();
        newItemDto.setName("Дрель");
        newItemDto.setDescription("Обычная дрель");
        newItemDto.setAvailable(true);

        when(itemClient.create(anyLong(), any())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newItemDto)))
                .andExpect(status().isOk());

        verify(itemClient, times(1)).create(anyLong(), any());
    }

    @Test
    @SneakyThrows
    void createItem_whenNameIsBlank_thenReturnsBadRequest() {
        NewItemDto newItemDto = new NewItemDto();
        newItemDto.setName("");
        newItemDto.setDescription("Обычная дрель");
        newItemDto.setAvailable(true);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newItemDto)))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).create(anyLong(), any());
    }
}