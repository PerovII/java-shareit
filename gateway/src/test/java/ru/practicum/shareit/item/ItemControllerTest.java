package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.NewCommentDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
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
    private ItemClient itemClient;

    private static final String HEADER = "X-Sharer-User-Id";

    @Test
    void create_whenValid_thenReturnsOk() throws Exception {
        long userId = 1L;
        NewItemDto dto = new NewItemDto();
        dto.setName("Item");
        dto.setDescription("Description");
        dto.setAvailable(true);

        when(itemClient.create(eq(userId), any())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/items")
                        .header(HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(itemClient).create(eq(userId), any());
    }

    @Test
    void update_whenValid_thenReturnsOk() throws Exception {
        long userId = 1L;
        long itemId = 1L;
        UpdateItemDto dto = new UpdateItemDto();
        dto.setName("Updated");

        when(itemClient.update(eq(userId), eq(itemId), any())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header(HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(itemClient).update(eq(userId), eq(itemId), any());
    }

    @Test
    void getItem_whenValid_thenReturnsOk() throws Exception {
        long userId = 1L;
        long itemId = 1L;

        when(itemClient.getItem(userId, itemId)).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header(HEADER, userId))
                .andExpect(status().isOk());

        verify(itemClient).getItem(userId, itemId);
    }

    @Test
    void getAllItems_whenValid_thenReturnsOk() throws Exception {
        long userId = 1L;

        when(itemClient.getAllItems(userId)).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/items")
                        .header(HEADER, userId))
                .andExpect(status().isOk());

        verify(itemClient).getAllItems(userId);
    }

    @Test
    void getSearchItems_whenValid_thenReturnsOk() throws Exception {
        String text = "search";

        when(itemClient.getSearchItems(text)).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(get("/items/search")
                        .param("text", text))
                .andExpect(status().isOk());

        verify(itemClient).getSearchItems(text);
    }

    @Test
    void createComment_whenValid_thenReturnsOk() throws Exception {
        long userId = 1L;
        long itemId = 1L;
        NewCommentDto dto = new NewCommentDto();
        dto.setText("Comment");

        when(itemClient.createComment(eq(userId), eq(itemId), any())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header(HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(itemClient).createComment(eq(userId), eq(itemId), any());
    }
}