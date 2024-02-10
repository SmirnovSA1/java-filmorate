package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController extends AbstractController<Film> {
    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) throws ValidationException {
        log.info("Adding film: {}", film);
        film.generateId();

        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Не удалось добавить фильм, " +
                    "т.к. дата релиза не может быть раньше даты рождения кино.");
        }

        films.put(film.getId(), film);
        return film;
    }

    @GetMapping
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) throws ValidationException {
        log.info("Updating film: {}", film);

        if (!films.containsKey(film.getId())) {
            throw new ValidationException("Не удалось обновить фильм, " +
                    "т.к. фильм по указанному id не найден.");
        }

        films.put(film.getId(), film);
        return film;
    }

    public void validate(Film film, String messagePath) throws ValidationException {
        if (film.getName() == null || film.getName().trim().isBlank()) {
            throw new ValidationException("Не удалось " + messagePath + " фильм, т.к. наименование не заполнено");
        }

        if (film.getDescription().trim().length() > 200) {
            throw new ValidationException("Не удалось " + messagePath +
                    " фильм, т.к. максимальная длина описания 200 символов.");
        }

        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Не удалось " + messagePath + " фильм, " +
                    "т.к. дата релиза не может быть раньше даты рождения кино.");
        }

        if (film.getDuration() < 0) {
            throw new ValidationException("Не удалось " + messagePath + " фильм, " +
                    "т.к. продолжительность фильма должна быть положительной.");
        }
    }
}
