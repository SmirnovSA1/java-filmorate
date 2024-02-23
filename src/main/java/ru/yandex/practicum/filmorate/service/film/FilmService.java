package ru.yandex.practicum.filmorate.service.film;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Getter
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public List<Film> getFilms() {
        log.info("Получение списка фильмов");
        return filmStorage.getFilms();
    }

    public Film getFilmById(Integer filmId) {
        log.info("Получение фильма по id: {}", filmId);
        return filmStorage.getFilmById(filmId);
    }

    public Film createFilm(Film film) {
        log.info("Добавление (создание) фильм: {}", film);
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        log.info("Обновление данных фильма: {}", film);
        return filmStorage.updateFilm(film);
    }

    public Map<String, String> deleteFilmById(Integer filmId) {
        log.info("Удаление фильма по id: {}", filmId);
        return filmStorage.deleteFilmById(filmId);
    }

    public Map<String, String> deleteAllFilms() {
        log.info("Удаление всех фильмов");
        return filmStorage.deleteAllFilms();
    }

    public Film addLikeToFilm(Integer filmId, Integer userId) {
        final Film film = filmStorage.getFilmById(filmId);
        final User user = userStorage.getUserById(userId);
        log.info("Пользователь {} добавляет лайк фильму {}", user.getName(), film.getName());

        film.getLikes().add(userId);
        log.info("Пользователь {} поставил лайка фильму {}", user.getName(), film.getName());
        return film;
    }

    public Film deleteLikeFromFilm(Integer filmId, Integer userId) {
        final Film film = filmStorage.getFilmById(filmId);
        final User user = userStorage.getUserById(userId);
        log.info("Пользователь {} удаляет лайк у фильма {}", user.getName(), film.getName());

        film.getLikes().remove(userId);
        log.info("Пользователь {} удалил лайк у фильма {}", user.getName(), film.getName());
        return film;
    }

    public List<Film> getPopularFilms(Integer count) {
        return filmStorage.getFilms().stream()
                .sorted((film1, film2) -> film2.getLikes().size() - film1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
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
