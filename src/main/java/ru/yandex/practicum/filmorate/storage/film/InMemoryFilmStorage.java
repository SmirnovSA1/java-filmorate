package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
@Qualifier("inMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {
    @Qualifier("inMemoryUserStorage")
    private final UserStorage userStorage;
    private HashMap<Integer, Film> films = new HashMap<>();
    private final List<Genre> genres = List.of(
            new Genre(1, "Комедия"),
            new Genre(2, "Триллер"),
            new Genre(3, "Боевик"),
            new Genre(4, "Драма"),
            new Genre(5, "Мелодрама"),
            new Genre(6, "Ужасы")
    );
    private final List<MPA> mpaList = List.of(
            new MPA(1, "G", 0),
            new MPA(2, "PG", 0),
            new MPA(3, "PG-13", 13),
            new MPA(4, "R", 17),
            new MPA(5, "NC-17", 18)
    );

    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilmById(Integer id) {
        final Film film = films.get(id);

        if (film == null) {
            log.error("Произошла ошибка при вызове метода getFilmById");
            throw new NotFoundException(String.format("Фильм с id: %s не найден", id));
        }

        log.info("Получен фильм: {} \n по id: {}", film.getName(), id);
        return film;
    }

    @Override
    public Film createFilm(Film film) {
        film.generateId();
        films.put(film.getId(), film);
        log.info("Фильм успешно создан: {}", film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            log.error("Произошла ошибка при вызове метода updateFilm");
            throw new NotFoundException(String.format("Фильм с id: %s не найден", film.getId()));
        }

        films.put(film.getId(), film);
        log.info("Данные фильма успешно обновлены: {}", film);
        return film;
    }

    @Override
    public Map<String, String> deleteFilmById(Integer id) {
        getFilmById(id);
        films.remove(id);
        log.info("Фильм по id: {} успешно удален", id);
        return Map.of("info", String.format("Фильм по id: %s успешно удален", id));
    }

    @Override
    public Map<String, String> deleteAllFilms() {
        films.clear();
        log.info("Все фильмы успешно удалены");
        return Map.of("info", String.format("Все фильмы успешно удалены"));
    }

    @Override
    public Film addLikeToFilm(Integer filmId, Integer userId) {
        final Film film = getFilmById(filmId);
        final User user = userStorage.getUserById(userId);


        film.getLikes().add(userId);
        log.info("Пользователь {} поставил лайка фильму {}", user.getName(), film.getName());
        return film;
    }

    @Override
    public Film deleteLikeFromFilm(Integer filmId, Integer userId) {
        final Film film = getFilmById(filmId);
        final User user = userStorage.getUserById(userId);
        log.info("Пользователь {} удаляет лайк у фильма {}", user.getName(), film.getName());

        film.getLikes().remove(userId);
        log.info("Пользователь {} удалил лайк у фильма {}", user.getName(), film.getName());
        return film;
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        return getFilms().stream()
                .sorted((film1, film2) -> film2.getLikes().size() - film1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public List<Genre> getAllGenres() {
        return genres;
    }

    @Override
    public Genre getGenreById(Integer genreId) {
        return genres.get(genreId);
    }

    @Override
    public List<MPA> getAllMPA() {
        return mpaList;
    }

    @Override
    public MPA getMPAById(Integer mpaId) {
        return mpaList.get(mpaId);
    }
}
