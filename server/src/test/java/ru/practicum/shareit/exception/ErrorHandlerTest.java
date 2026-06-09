package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ErrorHandlerTest {

    private final ErrorHandler errorHandler = new ErrorHandler();

    @Test
    void handleThrowable() {
        Throwable exception = new Throwable("Какой-то сбой");
        ErrorResponse response = errorHandler.handleThrowable(exception);
        assertNotNull(response);
        assertEquals("Произошла непредвиденная ошибка", response.getError());
    }

    @Test
    void emailExists() {
        EmailExistsException exception = new EmailExistsException("Email exists");
        ErrorResponse response = errorHandler.emailExists(exception);
        assertNotNull(response);
        assertEquals("Email exists", response.getError());
    }

    @Test
    void handleNotFound_emptyData() {
        EmptyDataException exception = new EmptyDataException("Empty data");
        ErrorResponse response = errorHandler.handleNotFound(exception);
        assertNotNull(response);
        assertEquals("Empty data", response.getError());
    }

    @Test
    void handleValidationException() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("objectName", "fieldName", "Ошибка валидации поля");

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        ErrorResponse response = errorHandler.handleValidationException(exception);
        assertNotNull(response);
        assertEquals("Ошибка валидации поля", response.getError());
    }

    @Test
    void notFound() {
        NotFoundException exception = new NotFoundException("Not found");
        ErrorResponse response = errorHandler.notFound(exception);
        assertNotNull(response);
        assertEquals("Not found", response.getError());
    }

    @Test
    void userValidation() {
        UserValidationException exception = new UserValidationException("Invalid user");
        ErrorResponse response = errorHandler.userValidation(exception);
        assertNotNull(response);
        assertEquals("Invalid user", response.getError());
    }

    @Test
    void handleMissingHeader() {
        MethodParameter parameter = mock(MethodParameter.class);
        MissingRequestHeaderException exception = new MissingRequestHeaderException("X-Sharer-User-Id", parameter);
        ErrorResponse response = errorHandler.handleMissingHeader(exception);
        assertNotNull(response);
        assertEquals("Отсутствует обязательный заголовок: X-Sharer-User-Id", response.getError());
    }

    @Test
    void handleTypeMismatch() {
        MethodArgumentTypeMismatchException exception = mock(MethodArgumentTypeMismatchException.class);
        when(exception.getName()).thenReturn("stateParam");
        ErrorResponse response = errorHandler.handleTypeMismatch(exception);
        assertNotNull(response);
        assertEquals("Неверный тип данных для параметра: stateParam", response.getError());
    }

    @Test
    void itemAvailabilityException() {
        ItemAvailabilityException exception = new ItemAvailabilityException("Not available");
        ErrorResponse response = errorHandler.itemAvailabilityException(exception);
        assertNotNull(response);
        assertEquals("Not available", response.getError());
    }

    @Test
    void commentValidationException() {
        CommentValidationException exception = new CommentValidationException("Invalid comment");
        ErrorResponse response = errorHandler.commentValidationException(exception);
        assertNotNull(response);
        assertEquals("Invalid comment", response.getError());
    }
}