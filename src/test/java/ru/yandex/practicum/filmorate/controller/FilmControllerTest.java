package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    private final static FilmController filmController = new FilmController();

    @AfterEach
    void clean() {
        filmController.films.clear();
    }

    @Test
    void addFilmValid() throws ValidationException {
        final Film film = new Film(1, "Защитник",
                "биографический фильм режиссёра Питера Ландесмана с Уиллом Смитом",
                LocalDate.of(2015, 11, 10), 123);
        filmController.validate(film, "добавить");
    }

    @Test
    void addFilmInvalid() {
        final Film film = new Film();
        Exception exc = assertThrows(ValidationException.class, () -> filmController.validate(film,
                "добавить"));
        assertEquals("Не удалось добавить фильм, т.к. наименование не заполнено", exc.getMessage());

        film.setName("");
        exc = assertThrows(ValidationException.class, () -> filmController.validate(film, "добавить"));
        assertEquals("Не удалось добавить фильм, т.к. наименование не заполнено", exc.getMessage());

        film.setName("Хоббит");
        film.setDescription("x".repeat(201));
        exc = assertThrows(ValidationException.class, () -> filmController.validate(film, "добавить"));
        assertEquals("Не удалось добавить фильм, т.к. максимальная длина описания 200 символов.",
                exc.getMessage());

        film.setDescription("x".repeat(100));
        film.setReleaseDate(LocalDate.of(1888, 12, 12));
        exc = assertThrows(ValidationException.class, () -> filmController.validate(film, "добавить"));
        assertEquals("Не удалось добавить фильм, " +
                        "т.к. дата релиза не может быть раньше даты рождения кино.", exc.getMessage());

        film.setReleaseDate(LocalDate.of(1988, 12, 12));
        film.setDuration(-1L);
        exc = assertThrows(ValidationException.class, () -> filmController.validate(film, "добавить"));
        assertEquals("Не удалось добавить фильм, " +
                "т.к. продолжительность фильма должна быть положительной.", exc.getMessage());
    }

    @Test
    void getFilms() {
        assertEquals(filmController.films.size(), 0);

        final Film film = new Film(1, "Защитник",
                "биографический фильм режиссёра Питера Ландесмана с Уиллом Смитом",
                LocalDate.of(2015, 11, 10), 123);
        filmController.films.put(film.getId(), film);
        assertEquals(filmController.films.size(), 1);
    }

    @Test
    void updateFilmValid() throws ValidationException {
        final Film film = new Film(1, "Защитник",
                "биографический фильм режиссёра Питера Ландесмана с Уиллом Смитом",
                LocalDate.of(2015, 11, 10), 123);
        filmController.films.put(film.getId(), film);

        film.setDescription("Новое описание фильма Защитник");
        filmController.films.put(film.getId(), film);

        assertEquals("Новое описание фильма Защитник", film.getDescription());
    }

    @Test
    void updateFilmInvalid() {
        final Film film = new Film();
        Exception exc = assertThrows(ValidationException.class, () -> filmController.validate(film, "обновить"));
        assertEquals("Не удалось обновить фильм, т.к. наименование не заполнено", exc.getMessage());

        film.setName("");
        exc = assertThrows(ValidationException.class, () -> filmController.validate(film, "обновить"));
        assertEquals("Не удалось обновить фильм, т.к. наименование не заполнено", exc.getMessage());

        film.setName("Хоббит");
        film.setDescription("x".repeat(201));
        exc = assertThrows(ValidationException.class, () -> filmController.validate(film, "обновить"));
        assertEquals("Не удалось обновить фильм, т.к. максимальная длина описания 200 символов.",
                exc.getMessage());

        film.setDescription("x".repeat(100));
        film.setReleaseDate(LocalDate.of(1888, 12, 12));
        exc = assertThrows(ValidationException.class, () -> filmController.validate(film, "обновить"));
        assertEquals("Не удалось обновить фильм, " +
                "т.к. дата релиза не может быть раньше даты рождения кино.", exc.getMessage());

        film.setReleaseDate(LocalDate.of(1988, 12, 12));
        film.setDuration(-1L);
        exc = assertThrows(ValidationException.class, () -> filmController.validate(film, "обновить"));
        assertEquals("Не удалось обновить фильм, " +
                "т.к. продолжительность фильма должна быть положительной.", exc.getMessage());
    }
}