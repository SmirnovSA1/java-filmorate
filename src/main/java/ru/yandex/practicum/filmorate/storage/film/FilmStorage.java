package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Map;

public interface FilmStorage {
    public List<Film> getFilms();
    public Film getFilmById(Integer id);
    public Film createFilm(Film film);
    public Film updateFilm(Film film);
    public Map<String, String> deleteFilmById(Integer id);
    public Map<String, String> deleteAllFilms();
}
