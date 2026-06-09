package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
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
    void handleThrowable_returnsErrorResponse() {
        Throwable exception = new Throwable("Скрытая ошибка базы данных");

        ErrorResponse response = errorHandler.handleThrowable(exception);

        assertNotNull(response);
        assertEquals("Произошла непредвиденная ошибка", response.getError());
    }

    @Test
    void handleValidationException_returnsErrorResponse() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("dto", "email", "Некорректный email");

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        ErrorResponse response = errorHandler.handleValidationException(exception);

        assertNotNull(response);
        assertEquals("Некорректный email", response.getError());
    }

    @Test
    void handleMissingHeader_returnsErrorResponse() {
        MissingRequestHeaderException exception = mock(MissingRequestHeaderException.class);
        when(exception.getHeaderName()).thenReturn("X-Sharer-User-Id");

        ErrorResponse response = errorHandler.handleMissingHeader(exception);

        assertNotNull(response);
        assertEquals("Отсутствует обязательный заголовок: X-Sharer-User-Id", response.getError());
    }

    @Test
    void handleTypeMismatch_returnsErrorResponse() {
        MethodArgumentTypeMismatchException exception = mock(MethodArgumentTypeMismatchException.class);
        when(exception.getName()).thenReturn("from");

        ErrorResponse response = errorHandler.handleTypeMismatch(exception);

        assertNotNull(response);
        assertEquals("Неверный тип данных для параметра: from", response.getError());
    }

    @Test
    void handleIllegalArgumentException_returnsErrorResponse() {
        IllegalArgumentException exception = new IllegalArgumentException("Unknown state: UNSUPPORTED_STATUS");

        ErrorResponse response = errorHandler.handleIllegalArgumentException(exception);

        assertNotNull(response);
        assertEquals("Unknown state: UNSUPPORTED_STATUS", response.getError());
    }
}