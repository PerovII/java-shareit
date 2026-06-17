package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.exception.EmailExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void getUserById_whenUserFound_thenReturnUserDto() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setName("John");
        user.setEmail("john@mail.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserDto actualDto = userService.getUserById(userId);

        assertEquals(userId, actualDto.getId());
        assertEquals("John", actualDto.getName());
        assertEquals("john@mail.com", actualDto.getEmail());
    }

    @Test
    void getUserById_whenUserNotFound_thenThrowNotFoundException() {
        long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUserById(userId));
    }

    @Test
    void create_whenValid_thenReturnUserDto() {
        NewUserDto newUserDto = new NewUserDto();
        newUserDto.setName("John");
        newUserDto.setEmail("john@mail.com");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName("John");
        savedUser.setEmail("john@mail.com");

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserDto actualDto = userService.create(newUserDto);

        assertEquals(1L, actualDto.getId());
        assertEquals("John", actualDto.getName());
        assertEquals("john@mail.com", actualDto.getEmail());
    }

    @Test
    void create_whenEmailExists_thenThrowEmailExistsException() {
        NewUserDto newUserDto = new NewUserDto();
        newUserDto.setName("John");
        newUserDto.setEmail("john@mail.com");

        when(userRepository.save(any(User.class))).thenThrow(new DataIntegrityViolationException(""));

        assertThrows(EmailExistsException.class, () -> userService.create(newUserDto));
    }

    @Test
    void delete_whenUserExists_thenDeleteSuccessfully() {
        long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);

        userService.delete(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void delete_whenUserNotFound_thenThrowNotFoundException() {
        long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> userService.delete(userId));
        verify(userRepository, never()).deleteById(userId);
    }

    @Test
    void update_whenValid_thenReturnUpdatedUserDto() {
        long userId = 1L;
        UpdateUserDto updateDto = new UpdateUserDto();
        updateDto.setName("John Updated");
        updateDto.setEmail("updated@mail.com");

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("John");
        existingUser.setEmail("john@mail.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDto actualDto = userService.update(userId, updateDto);

        assertEquals(userId, actualDto.getId());
        assertEquals("John Updated", actualDto.getName());
        assertEquals("updated@mail.com", actualDto.getEmail());
    }

    @Test
    void update_whenUserNotFound_thenThrowNotFoundException() {
        long userId = 1L;
        UpdateUserDto updateDto = new UpdateUserDto();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.update(userId, updateDto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void update_whenEmailExists_thenThrowEmailExistsException() {
        long userId = 1L;
        UpdateUserDto updateDto = new UpdateUserDto();
        updateDto.setEmail("existing@mail.com");

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("John");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenThrow(new DataIntegrityViolationException(""));

        assertThrows(EmailExistsException.class, () -> userService.update(userId, updateDto));
    }
}