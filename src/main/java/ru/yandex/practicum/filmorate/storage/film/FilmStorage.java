package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.List;
import java.util.Map;

public interface FilmStorage {
    public List<Film> getFilms();

    public Film getFilmById(Integer id);

    public Film createFilm(Film film);

    public Film updateFilm(Film film);

    public Map<String, String> deleteFilmById(Integer id);

    public Map<String, String> deleteAllFilms();

    public Film addLikeToFilm(Integer filmId, Integer userId);

    public Film deleteLikeFromFilm(Integer filmId, Integer userId);

    public List<Film> getPopularFilms(Integer count);

    public List<Genre> getAllGenres();

    public Genre getGenreById(Integer genreId);

    public List<MPA> getAllMPA();

    public MPA getMPAById(Integer mpaId);
}
