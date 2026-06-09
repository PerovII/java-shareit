package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class ItemMapperTest {

    @Test
    void mapToItem_whenValidDto_thenReturnItem() {
        NewItemDto dto = new NewItemDto();
        dto.setName("Дрель");
        dto.setDescription("Мощная");
        dto.setAvailable(true);

        Item item = ItemMapper.mapToItem(dto);

        assertNotNull(item);
        assertEquals("Дрель", item.getName());
        assertEquals("Мощная", item.getDescription());
        assertTrue(item.isAvailable());
    }

    @Test
    void mapToItemDto_whenValidItem_thenReturnDto() {
        User owner = new User();
        owner.setId(1L);

        Item item = new Item();
        item.setId(1L);
        item.setName("Дрель");
        item.setDescription("Мощная");
        item.setAvailable(true);
        item.setOwner(owner);

        ItemDto dto = ItemMapper.mapToItemDto(item, Collections.emptyList());

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Дрель", dto.getName());
        assertEquals("Мощная", dto.getDescription());
        assertTrue(dto.getAvailable());
    }
}