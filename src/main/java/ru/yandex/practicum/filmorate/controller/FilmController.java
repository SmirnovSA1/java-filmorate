package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class FilmController {
    private final FilmService filmService;

    @GetMapping("/films")
    public List<Film> getFilms() {
        log.info("Получение списка фильмов");
        return filmService.getFilms();
    }

    @GetMapping("/films/{id}")
    public Film getFilmById(@PathVariable(name = "id") Integer filmId) {
        log.info("Получение фильма по id: {}", filmId);
        return filmService.getFilmById(filmId);
    }

    @PostMapping("/films")
    @ResponseStatus(HttpStatus.CREATED)
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("Добавление (создание) фильма: {}", film.getName());
        return filmService.createFilm(film);
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Обновление данных фильма: {}", film);
        return filmService.updateFilm(film);
    }

    @DeleteMapping("/films/{id}")
    public Map<String, String> deleteFilmById(@PathVariable(name = "id") Integer filmId) {
        log.info("Удаление фильма по id: {}", filmId);
        return filmService.deleteFilmById(filmId);
    }

    @DeleteMapping("/films")
    public Map<String, String> deleteAllFilms() {
        log.info("Удаление всех фильмов");
        return filmService.deleteAllFilms();
    }

    @PutMapping("/films/{id}/like/{userId}")
    public Film addLikeToFilm(@PathVariable(name = "id") Integer filmId,
                              @PathVariable(name = "userId") Integer userId) {
        log.info("Пользователь с id {} добавляет лайк фильму с id {}", userId, filmId);
        return filmService.addLikeToFilm(filmId, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public Film deleteFilmFromFilm(@PathVariable(name = "id") Integer filmId,
                                   @PathVariable(name = "userId") Integer userId) {
        log.info("Пользователь с id {} удаляет лайк у фильма с id {}", userId, filmId);
        return filmService.deleteLikeFromFilm(filmId, userId);
    }

    @GetMapping("/films/popular")
    public List<Film> getPopularFilm(@RequestParam(name = "count", defaultValue = "10") String count) {
        log.info("Получение топ-{} фильмов", count);
        Integer intCount = Integer.parseInt(count);
        return filmService.getPopularFilms(intCount);
    }

    @GetMapping("/genres")
    public List<Genre> gerAllGenres() {
        log.info("Получение списка всех жанров");
        return filmService.getAllGenres();
    }

    @GetMapping("/genres/{id}")
    public Genre getGenreById(@PathVariable(name = "id") Integer genreId) {
        log.info("Получение жанра по id {}", genreId);
        return filmService.getGenreById(genreId);
    }

    @GetMapping("/mpa")
    public List<MPA> getAllMPA() {
        log.info("Получение списка всех рейтингов возрастного ограничения");
        return filmService.getAllMPA();
    }

    @GetMapping("/mpa/{id}")
    public MPA getMPAById(@PathVariable(name = "id") Integer mpaId) {
        log.info("Получение рейнтинга возрастного ограничения по id {}", mpaId);
        return filmService.getMPAById(mpaId);
    }
}
