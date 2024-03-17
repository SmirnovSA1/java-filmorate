package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.AlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.storage.film.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.storage.film.mapper.MPAMapper;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Component
@Slf4j
@Primary
@RequiredArgsConstructor
@Qualifier("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    @Qualifier("userDbStorage")
    private final UserStorage userStorage;

    @Override
    public List<Film> getFilms() {
        String query = "SELECT " +
                "f.*, " +
                "m.mpa_name, " +
                "m.mpa_min_age " +
                "FROM films f " +
                "JOIN mpa m ON f.film_mpa_id = m.mpa_id";
        return jdbcTemplate.query(query, new FilmMapper(jdbcTemplate));
    }

    @Override
    public Film getFilmById(Integer id) {
        String query = "SELECT " +
                "f.*, " +
                "m.mpa_name, " +
                "m.mpa_min_age " +
                "FROM films f " +
                "JOIN mpa m ON f.film_mpa_id = m.mpa_id " +
                "WHERE film_id = ?";
        Film foundFilm;

        try {
            foundFilm = jdbcTemplate.queryForObject(query, new FilmMapper(jdbcTemplate), id);
            return foundFilm;
        } catch (RuntimeException e) {
            log.info("Фильм с id {} не найден", id);
            throw new NotFoundException(String.format("Фильм по id %d не найден", id));
        }
    }

    @Override
    public Film createFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");

        int id = simpleJdbcInsert.executeAndReturnKey(
                Map.of(
                        "film_name", film.getName(),
                        "film_description", film.getDescription(),
                        "film_release_date", film.getReleaseDate(),
                        "film_duration", film.getDuration(),
                        "film_mpa_id", film.getMpa().getId()))
                .intValue();

        log.info("Создан фильм с id {} в таблице films", id);

        film.setId(id);
        checkFilmGenres(film);

        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String query = "UPDATE films SET film_name = ?, " +
                "film_description = ?, " +
                "film_release_date = ?, " +
                "film_duration = ?, " +
                "film_mpa_id = ? " +
                "WHERE film_id = ?;";
        int countUpdatedLines = jdbcTemplate.update(query,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        if (countUpdatedLines == 0) {
            log.info("Фильм с id {} не найден", film.getId());
            throw new NotFoundException(String.format("Фильм с id %d не найден", film.getId()));
        }

        log.info("Фильм с id {} обновлен", film.getId());

        checkFilmGenres(film);

        return film;
    }

    @Override
    public Map<String, String> deleteFilmById(Integer id) {
        String query = "DELETE " +
                "FROM films " +
                "WHERE film_id = ?";
        int deleteLine = jdbcTemplate.update(query, id);

        if (deleteLine == 0) {
            log.info("Фильм для удаления по id {} не найден", id);
            throw new NotFoundException(String.format("Фильм для удаления по id %d не найден", id));
        }

        return Map.of("info", String.format("Фильм по id: %d успешно удален", id));
    }

    @Override
    public Map<String, String> deleteAllFilms() {
        String query = "DELETE FROM films";
        int deleteLines = jdbcTemplate.update(query);

        if (deleteLines == 0) {
            log.info("Не найдено ни одного фильма для удаления не найдено");
            throw new NotFoundException(String.format("Не найдено ни одного фильма для удаления не найдено"));
        }

        return Map.of("info", String.format("Все фильмы удалены"));
    }

    @Override
    public Film addLikeToFilm(Integer filmId, Integer userId) {
        final Film foundFilm = getFilmById(filmId);
        final User foundUser = userStorage.getUserById(userId);

        // проверим, есть ли лайк фильму от пользователя
        String query = "SELECT " +
                "COUNT(film_like_id) " +
                "FROM film_likes " +
                "WHERE film_id = ? AND user_id = ?";
        int foundLine = jdbcTemplate.queryForObject(query, Integer.class, filmId, userId);

        if (foundLine > 0) {
            log.info("Пользователь с id {} уже поставил лайк фильму с id {}", userId, filmId);
            throw new AlreadyExistException(String.format("Пользователь с id %d уже поставил лайк фильму с id %d",
                    userId, filmId));
        }

        jdbcTemplate.update("INSERT INTO film_likes (film_id, user_id) VALUES (?, ?);", filmId, userId);
        log.info("Пользователь с id {} поставил лайк фильму с id {}", userId, filmId);

        final Film updatedFilm = getFilmById(filmId);

        return updatedFilm;
    }

    @Override
    public Film deleteLikeFromFilm(Integer filmId, Integer userId) {
        final Film foundFilm = getFilmById(filmId);
        final User foundUser = userStorage.getUserById(userId);

        String query = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
        int deletedLine = jdbcTemplate.update(query, filmId, userId);

        if (deletedLine == 0) {
            log.info("Не найден лайк фильму с id {} от пользователя с id {}", filmId, userId);
            throw new NotFoundException(String.format("Не найден лайк фильму с id %d от пользователя с id %d",
                    filmId, userId));
        }

        final Film updatedFilm = getFilmById(filmId);

        return updatedFilm;
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        String query = "SELECT " +
                "f.*, " +
                "m.mpa_id, " +
                "m.mpa_name, " +
                "m.mpa_min_age " +
                "FROM films f " +
                "JOIN mpa AS m ON f.film_mpa_id = m.mpa_id " +
                "JOIN film_likes AS fl ON f.film_id = fl.film_id " +
                "GROUP BY f.film_id " +
                "ORDER BY COUNT(fl.user_id) DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(query, new FilmMapper(jdbcTemplate), count);
    }

    @Override
    public List<Genre> getAllGenres() {
        String query = "SELECT * FROM genres;";

        return jdbcTemplate.query(query, new GenreMapper());
    }

    @Override
    public Genre getGenreById(Integer genreId) {
        String query = "SELECT * FROM genres WHERE genre_id = ?;";
        Genre foundGenre;

        try {
            foundGenre = jdbcTemplate.queryForObject(query, new GenreMapper(), genreId);
            return foundGenre;
        } catch (RuntimeException e) {
            log.info("Жанр по id {} не найден", genreId);
            throw new NotFoundException(String.format("Жанр по id %d не найден", genreId));
        }
    }

    @Override
    public List<MPA> getAllMPA() {
        String query = "SELECT * FROM mpa;";

        return jdbcTemplate.query(query, new MPAMapper());
    }

    @Override
    public MPA getMPAById(Integer mpaId) {
        String query = "SELECT * FROM mpa WHERE mpa_id = ?;";
        MPA foundMPA;

        try {
            foundMPA = jdbcTemplate.queryForObject(query, new MPAMapper(), mpaId);
            return foundMPA;
        } catch (RuntimeException e) {
            log.info("Не найден рейтинг возрастного ограничения по id {}", mpaId);
            throw new NotFoundException(String.format("Не найден рейтинг возрастного ограничения по id %d", mpaId));
        }
    }

    private void checkFilmGenres(Film film) {
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(
                        "INSERT INTO films_genres (film_id, genre_id) VALUES (?, ?);",
                        film.getId(),
                        genre.getId()
                );
            }
        }
    }
}
