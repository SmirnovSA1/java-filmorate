package ru.yandex.practicum.filmorate.service.film;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Getter
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final LocalDate creationDate = LocalDate.of(1895, 12, 28);

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film getFilmById(Integer filmId) {
        return filmStorage.getFilmById(filmId);
    }

    public Film createFilm(Film film) {
        validate(film, "создать");
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        validate(film, "обновить");
        return filmStorage.updateFilm(film);
    }

    public Map<String, String> deleteFilmById(Integer filmId) {
        return filmStorage.deleteFilmById(filmId);
    }

    public Map<String, String> deleteAllFilms() {
        return filmStorage.deleteAllFilms();
    }

    public Film addLikeToFilm(Integer filmId, Integer userId) {
        return filmStorage.addLikeToFilm(filmId, userId);
    }

    public Film deleteLikeFromFilm(Integer filmId, Integer userId) {
        return filmStorage.deleteLikeFromFilm(filmId, userId);
    }

    public List<Film> getPopularFilms(Integer count) {
        return filmStorage.getPopularFilms(count);
    }

    public List<Genre> getAllGenres() {
        return filmStorage.getAllGenres();
    }

    public Genre getGenreById(Integer genreId) {
        return filmStorage.getGenreById(genreId);
    }

    public List<MPA> getAllMPA() {
        return filmStorage.getAllMPA();
    }

    public MPA getMPAById(Integer mpaId) {
        return filmStorage.getMPAById(mpaId);
    }

    public void validate(Film film, String messagePath) throws ValidationException {
        if (film.getName() == null || film.getName().trim().isBlank()) {
            throw new ValidationException("Не удалось " + messagePath + " фильм, т.к. наименование не заполнено");
        }

        if (film.getDescription().trim().length() > 200) {
            throw new ValidationException("Не удалось " + messagePath +
                    " фильм, т.к. максимальная длина описания 200 символов.");
        }

        if (film.getReleaseDate().isBefore(creationDate)) {
            throw new ValidationException("Не удалось " + messagePath + " фильм, " +
                    "т.к. дата релиза не может быть раньше даты создания кино.");
        }

        if (film.getDuration() < 0) {
            throw new ValidationException("Не удалось " + messagePath + " фильм, " +
                    "т.к. продолжительность фильма должна быть положительной.");
        }

        if (film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }

        if (film.getGenres() == null) {
            film.setGenres(new HashSet<>());
        }

        if (film.getMpa() == null) {
            film.setMpa(new MPA(1, "G", 0));
        } else if (film.getMpa().getId() > 5) {
            throw new RuntimeException("Указан несуществующий рейнтинг возрастного ограничения");
        }
    }
}
