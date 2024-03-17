package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
public class User {
    private Integer id;
    @NotBlank(message = "email обязателен к заполнению")
    @Email
    private String email;
    @NotBlank(message = "login обязателен к заполнению")
    private String login;
    private String name;
    @Past(message = "birthday должен быть раньше текущей даты")
    @NotNull(message = "birthday обязателен к заполнению")
    private LocalDate birthday;
    private Set<Integer> friends;
    private static int count = 0;

    public void generateId() {
        this.id = ++count;
    }

    public static void resetCount() {
        count = 0;
    }
}
