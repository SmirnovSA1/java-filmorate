package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
public class Film {
    private Integer id;
    @NotBlank(message = "Наименование не должно быть пустым")
    private String name;
    @Size(min = 1, max = 200, message = "Длина описания не должна быть меньше 1 и не больше 200 символов")
    private String description;
    @NotNull(message = "releaseDate обязателен к заполнению")
    private LocalDate releaseDate;
    @Min(value = 1, message = "Длительность фильма должна быть не меньше 1 минуты")
    private long duration;
    private Set<Genre> genres;
    private MPA mpa;
    private Set<Integer> likes;
    private static int count = 0;

    public void generateId() {
        this.id = ++count;
    }

    public static void resetCount() {
        count = 0;
    }
}
