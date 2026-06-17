package ru.practicum.shareit.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NewUserDto {
    private String name;
    private String email;
}
