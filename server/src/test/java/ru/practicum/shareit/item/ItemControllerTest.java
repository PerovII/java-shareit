package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    private static final String HEADER = "X-Sharer-User-Id";

    @Test
    void create() throws Exception {
        NewItemDto dto = new NewItemDto();
        dto.setName("Item");
        dto.setDescription("Desc");
        dto.setAvailable(true);

        when(itemService.create(anyLong(), any())).thenReturn(new ItemDto());

        mockMvc.perform(post("/items")
                        .header(HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void update() throws Exception {
        UpdateItemDto dto = new UpdateItemDto();
        dto.setName("Updated");

        when(itemService.update(anyLong(), anyLong(), any())).thenReturn(new ItemDto());

        mockMvc.perform(patch("/items/1")
                        .header(HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void getItem() throws Exception {
        when(itemService.getItemById(anyLong(), anyLong())).thenReturn(new ItemWithBookingDto());

        mockMvc.perform(get("/items/1")
                        .header(HEADER, 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getAll() throws Exception {
        when(itemService.getAll(anyLong())).thenReturn(List.of(new ItemWithBookingDto()));

        mockMvc.perform(get("/items")
                        .header(HEADER, 1L))
                .andExpect(status().isOk());
    }

    @Test
    void search() throws Exception {
        when(itemService.getSearchItems(anyString())).thenReturn(List.of(new ItemDto()));

        mockMvc.perform(get("/items/search")
                        .param("text", "text"))
                .andExpect(status().isOk());
    }

    @Test
    void createComment() throws Exception {
        NewCommentDto dto = new NewCommentDto();
        dto.setText("Comment");

        when(itemService.createComment(anyLong(), anyLong(), any())).thenReturn(new CommentDto());

        mockMvc.perform(post("/items/1/comment")
                        .header(HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }
}