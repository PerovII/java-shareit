package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplIntegrationTest {

    private final ItemService itemService;
    private final UserService userService;

    @Test
    void getAllUserItems_whenItemsExist_thenReturnItemsList() {

        NewUserDto ownerDto = new NewUserDto();
        ownerDto.setName("Иван Владелец");
        ownerDto.setEmail("owner@yandex.ru");
        UserDto owner = userService.create(ownerDto);

        NewUserDto otherUserDto = new NewUserDto();
        otherUserDto.setName("Петр Чужой");
        otherUserDto.setEmail("other@yandex.ru");
        userService.create(otherUserDto);

        NewItemDto item1 = new NewItemDto();
        item1.setName("Дрель");
        item1.setDescription("Мощная дрель");
        item1.setAvailable(true);
        itemService.create(owner.getId(), item1);

        NewItemDto item2 = new NewItemDto();
        item2.setName("Отвертка");
        item2.setDescription("Крестовая");
        item2.setAvailable(true);
        itemService.create(owner.getId(), item2);

        List<ItemWithBookingDto> userItems = itemService.getAll(owner.getId());

        assertThat(userItems)
                .isNotNull()
                .hasSize(2);

        assertThat(userItems.get(0).getName()).isEqualTo("Дрель");
        assertThat(userItems.get(1).getName()).isEqualTo("Отвертка");
    }
}