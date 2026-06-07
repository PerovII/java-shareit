package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplIntegrationTest {

    private final UserService userService;

    @Test
    void createUser_whenValidData_thenUserSavedInDatabase() {
        NewUserDto newUserDto = new NewUserDto();
        newUserDto.setName("Тест");
        newUserDto.setEmail("test@yandex.ru");

        UserDto savedUser = userService.create(newUserDto);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isPositive();
        assertThat(savedUser.getName()).isEqualTo("Тест");
        assertThat(savedUser.getEmail()).isEqualTo("test@yandex.ru");
    }
}