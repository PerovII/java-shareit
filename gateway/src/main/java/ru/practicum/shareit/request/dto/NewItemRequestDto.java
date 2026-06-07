package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewItemRequestDto {
    @NotBlank(message = "Описание не может быть пустым")
    private String description;
}