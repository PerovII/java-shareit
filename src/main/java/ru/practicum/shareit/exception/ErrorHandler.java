package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(Throwable exception) {
        log.error("Непредвиденная ошибка: {}", exception.getMessage());
        return new ErrorResponse(
                Map.of("error", "Произошла непредвиденная ошибка")
        );
    }

    @ExceptionHandler(EmailExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse emailExists(EmailExistsException exception) {
        log.warn("Ошибка: {}", exception.getMessage());
        return new ErrorResponse(
                Map.of("error", exception.getMessage())
        );
    }

    @ExceptionHandler(EmptyDataException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(EmptyDataException exception) {

        log.warn("Ошибка: {}", exception.getMessage());
        return new ErrorResponse(
                Map.of("error", exception.getMessage())
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(MethodArgumentNotValidException exception) {

        Map<String, String> errors = new LinkedHashMap<>();

        exception.getBindingResult().getFieldErrors().forEach(err ->
                errors.putIfAbsent(
                        err.getField(),
                        err.getDefaultMessage()
                )
        );

        log.warn("Ошибка валидации: {}", errors);
        return new ErrorResponse(errors);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse notFound(NotFoundException exception) {

        log.warn("Ошибка: {}", exception.getMessage());
        return new ErrorResponse(
                Map.of("error", exception.getMessage())
        );
    }

    @ExceptionHandler(UserValidationException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse userValidation(UserValidationException exception) {

        log.warn("Ошибка: {}", exception.getMessage());
        return new ErrorResponse(
                Map.of("error", exception.getMessage())
        );
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingHeader(MissingRequestHeaderException exception) {
        log.warn("Отсутствует обязательный заголовок: {}", exception.getHeaderName());
        return new ErrorResponse(
                Map.of("error", String.format("Отсутствует обязательный заголовок: %s", exception.getHeaderName()))
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleTypeMismatch(MethodArgumentTypeMismatchException exception) {
        log.warn("Неверный тип данных для параметра: {}", exception.getName());
        return new ErrorResponse(
                Map.of("error", String.format("Неверный тип данных для параметра: %s", exception.getName()))
        );
    }


}
