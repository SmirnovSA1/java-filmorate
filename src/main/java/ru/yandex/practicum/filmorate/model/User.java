package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Integer id;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z]+$")
    private String login;
    private String name;
    @Past
    @NotNull
    private LocalDate birthday;
    private static int count = 0;

    public void generateId() {
        this.id = ++count;
    }
}
