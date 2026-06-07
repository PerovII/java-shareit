package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    // Мокаем именно Service, так как мы находимся в модуле server
    @MockBean
    private UserService userService;

    @Test
    @SneakyThrows
    void getUser_whenUserExists_thenReturnsUserDto() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Ivan");
        userDto.setEmail("ivan@yandex.ru");

        when(userService.getUserById(anyLong())).thenReturn(userDto);

        mockMvc.perform(get("/users/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Ivan"))
                .andExpect(jsonPath("$.email").value("ivan@yandex.ru"));

        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    @SneakyThrows
    void create_whenInvoked_thenReturnsUserDto() {
        NewUserDto newUserDto = new NewUserDto();
        newUserDto.setName("Ivan");
        newUserDto.setEmail("ivan@yandex.ru");

        UserDto expectedDto = new UserDto();
        expectedDto.setId(1L);
        expectedDto.setName("Ivan");
        expectedDto.setEmail("ivan@yandex.ru");

        when(userService.create(any())).thenReturn(expectedDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUserDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Ivan"));

        verify(userService, times(1)).create(any());
    }

    @Test
    @SneakyThrows
    void deleteUser_whenInvoked_thenReturnsStatusOk() {
        mockMvc.perform(delete("/users/{id}", 1L))
                .andExpect(status().isOk());

        verify(userService, times(1)).delete(1L);
    }
}