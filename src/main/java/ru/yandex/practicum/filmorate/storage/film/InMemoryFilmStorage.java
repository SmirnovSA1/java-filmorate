package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final LocalDate creationDate = LocalDate.of(1895, 12, 28);
    private HashMap<Integer, Film> films = new HashMap<>();

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
        if (film.getReleaseDate().isBefore(creationDate)) {
            log.error("Произошла ошибка при вызове метода createFilm");
            throw new ValidationException("Не удалось добавить фильм, " +
                    "т.к. дата релиза не может быть раньше даты создания кино.");
        }

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
}
