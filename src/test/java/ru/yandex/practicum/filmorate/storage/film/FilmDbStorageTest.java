package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FilmDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private FilmService filmService;
    @Qualifier("filmDbStorage")
    private FilmStorage filmStorage;
    @Qualifier("userDbStorage")
    private UserStorage userStorage;

    @BeforeEach
    void setUp() {
        userStorage = new UserDbStorage(jdbcTemplate);
        filmStorage = new FilmDbStorage(jdbcTemplate, userStorage);
        filmService = new FilmService(filmStorage);

        List<MPA> mpaSet = List.of(
                new MPA(1, "G", 0),
                new MPA(2, "PG", 0),
                new MPA(3, "PG-13", 13),
                new MPA(4, "R", 17),
                new MPA(5, "NC-17", 18)
                );
    }

    @Test
    void getFilms() {
        Film film1 = new Film(1, "Film №1", "Description about film №1",
                LocalDate.of(1984, 3, 15), 127,
                Set.of(new Genre(1, "Комедия"), new Genre(2, "Драма")),
                new MPA(4, "R", 17), new HashSet<>());
        Film film2 = new Film(2, "Film №2", "Description about film №2",
                LocalDate.of(1998, 11, 12), 98,
                Set.of(new Genre(3, "Мультфильм")),
                new MPA(2, "PG", 0), new HashSet<>());
        Film film3 = new Film(3, "Film №3", "Description about film №3",
                LocalDate.of(2011, 4, 23), 162,
                Set.of(new Genre(4, "Триллер"),
                        new Genre(5, "Документальный"),
                        new Genre(6, "Боевик")),
                new MPA(5, "NC-17", 18), new HashSet<>());

        filmStorage.createFilm(film1);
        filmStorage.createFilm(film2);
        filmStorage.createFilm(film3);

        List<Film> allFilms = filmStorage.getFilms();

        assertThat(allFilms)
                .isNotNull()
                .isNotEmpty()
                .hasSameSizeAs(List.of(film1, film2, film3))
                .hasSize(3)
                .contains(film1, film2, film3);
    }

    @Test
    void getFilmById() {
        Film film1 = new Film(1, "Film №1", "Description about film №1",
                LocalDate.of(1984, 3, 15), 127,
                Set.of(new Genre(1, "Комедия"), new Genre(2, "Драма")),
                new MPA(4, "R", 17), new HashSet<>());

        filmStorage.createFilm(film1);

        Film foundFilm = filmStorage.getFilmById(1);

        assertThat(foundFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(film1);
    }

    @Test
    void createFilm() {
        Film film1 = new Film(1, "Film №1", "Description about film №1",
                LocalDate.of(1984, 3, 15), 127,
                Set.of(new Genre(1, "Комедия"), new Genre(2, "Драма")),
                new MPA(4, "R", 17), new HashSet<>());

        filmService.validate(film1, "добавить");
        Film createdFilm = filmStorage.createFilm(film1);

        assertThat(createdFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(film1)
                .hasSameHashCodeAs(film1);
    }

    @Test
    void updateFilm() {
        Film film1 = new Film(1, "Film №1", "Description about film №1",
                LocalDate.of(1984, 3, 15), 127,
                Set.of(new Genre(1, "Комедия"), new Genre(2, "Драма")),
                new MPA(4, "R", 17), new HashSet<>());

        filmStorage.createFilm(film1);
        film1.setMpa(new MPA(3, "PG-13", 13));
        filmService.validate(film1, "добавить");
        Film updatedFilm = filmStorage.updateFilm(film1);

        assertThat(updatedFilm)
                .isNotNull()
                .matches(user -> user.getMpa().equals(new MPA(3, "PG-13", 13)))
                .usingRecursiveComparison()
                .isEqualTo(film1);
    }

    @Test
    void deleteFilmById() {
        Film film1 = new Film(1, "Film №1", "Description about film №1",
                LocalDate.of(1984, 3, 15), 127,
                Set.of(new Genre(1, "Комедия"), new Genre(2, "Драма")),
                new MPA(4, "R", 17), new HashSet<>());
        filmStorage.createFilm(film1);

        List<Film> allFilms = filmStorage.getFilms();

        assertThat(allFilms)
                .isNotNull()
                .isNotEmpty()
                .hasSameSizeAs(List.of(film1))
                .hasSize(1)
                .contains(film1);

        Map<String, String> response = filmStorage.deleteFilmById(1);

        assertThat(response)
                .containsKey("info")
                .contains(Map.entry("info","Фильм по id: 1 успешно удален"))
                .containsEntry("info", "Фильм по id: 1 успешно удален");
    }

    @Test
    void deleteAllFilms() {
        Film film1 = new Film(1, "Film №1", "Description about film №1",
                LocalDate.of(1984, 3, 15), 127,
                Set.of(new Genre(1, "Комедия"), new Genre(2, "Драма")),
                new MPA(4, "R", 17), new HashSet<>());
        Film film2 = new Film(2, "Film №2", "Description about film №2",
                LocalDate.of(1998, 11, 12), 98,
                Set.of(new Genre(3, "Мультфильм")),
                new MPA(2, "PG", 0), new HashSet<>());
        Film film3 = new Film(3, "Film №3", "Description about film №3",
                LocalDate.of(2011, 4, 23), 162,
                Set.of(new Genre(4, "Триллер"),
                        new Genre(5, "Документальный"),
                        new Genre(6, "Боевик")),
                new MPA(5, "NC-17", 18), new HashSet<>());

        filmStorage.createFilm(film1);
        filmStorage.createFilm(film2);
        filmStorage.createFilm(film3);

        List<Film> allFilms = filmStorage.getFilms();

        assertThat(allFilms)
                .isNotNull()
                .isNotEmpty()
                .hasSameSizeAs(List.of(film1, film2, film3))
                .hasSize(3)
                .contains(film1, film2, film3);

        Map<String, String> response = filmStorage.deleteAllFilms();

        assertThat(response)
                .containsKey("info")
                .contains(Map.entry("info","Все фильмы удалены"))
                .containsEntry("info", "Все фильмы удалены");
    }

    @Test
    void addLikeToFilm() {
        Film film1 = new Film(1, "Film №1", "Description about film №1",
                LocalDate.of(1984, 3, 15), 127,
                Set.of(new Genre(1, "Комедия"), new Genre(2, "Драма")),
                new MPA(4, "R", 17), new HashSet<>());
        filmStorage.createFilm(film1);

        User user1 = new User(1, "user@email.ru", "vanya123", "Ivan Petrov",
                LocalDate.of(1990, 1, 1), new HashSet<>());
        User user2 = new User(2, "user2@email.ru", "vasya321", "Vasya Ivanov",
                LocalDate.of(1992, 2, 2), new HashSet<>());
        userStorage.createUser(user1);
        userStorage.createUser(user2);

        filmStorage.addLikeToFilm(1, 1);
        Film likedFilm = filmStorage.addLikeToFilm(1, 2);
        Set<Integer> likeSet = likedFilm.getLikes();

        assertThat(likeSet)
                .isNotNull()
                .contains(1, 2)
                .hasSize(2)
                .isNotEmpty();
    }

    @Test
    void deleteLikeFromFilm() {
        Film film1 = new Film(1, "Film №1", "Description about film №1",
                LocalDate.of(1984, 3, 15), 127,
                Set.of(new Genre(1, "Комедия"), new Genre(2, "Драма")),
                new MPA(4, "R", 17), new HashSet<>());
        filmStorage.createFilm(film1);

        User user1 = new User(1, "user@email.ru", "vanya123", "Ivan Petrov",
                LocalDate.of(1990, 1, 1), new HashSet<>());
        User user2 = new User(2, "user2@email.ru", "vasya321", "Vasya Ivanov",
                LocalDate.of(1992, 2, 2), new HashSet<>());
        userStorage.createUser(user1);
        userStorage.createUser(user2);

        filmStorage.addLikeToFilm(1, 1);
        filmStorage.addLikeToFilm(1, 2);

        Film dislikedFilm = filmStorage.deleteLikeFromFilm(1, 1);
        Set<Integer> likeSet = dislikedFilm.getLikes();

        assertThat(likeSet)
                .isNotNull()
                .contains(2)
                .hasSize(1)
                .isNotEmpty();

        Film againDislikedFilm = filmStorage.deleteLikeFromFilm(1, 2);
        Set<Integer> anotherLikeSet = againDislikedFilm.getLikes();

        assertThat(anotherLikeSet)
                .isNotNull()
                .doesNotContain(1, 2)
                .hasSize(0)
                .isEmpty();
    }

    @Test
    void getPopularFilms() {
        Film film1 = new Film(1, "Film №1", "Description about film №1",
                LocalDate.of(1984, 3, 15), 127,
                Set.of(new Genre(1, "Комедия"), new Genre(2, "Драма")),
                new MPA(4, "R", 17), new HashSet<>());
        Film film2 = new Film(2, "Film №2", "Description about film №2",
                LocalDate.of(1998, 11, 12), 98,
                Set.of(new Genre(3, "Мультфильм")),
                new MPA(2, "PG", 0), new HashSet<>());
        Film film3 = new Film(3, "Film №3", "Description about film №3",
                LocalDate.of(2011, 4, 23), 162,
                Set.of(new Genre(4, "Триллер"),
                        new Genre(5, "Документальный"),
                        new Genre(6, "Боевик")),
                new MPA(5, "NC-17", 18), new HashSet<>());
        Film film4 = new Film(4, "Film №4", "Description about film №4",
                LocalDate.of(1966, 12, 02), 118,
                Set.of(new Genre(1, "Комедия"),
                        new Genre(3, "Мультфильм")),
                new MPA(3, "PG-13", 13), new HashSet<>());
        Film film5 = new Film(5, "Film №5", "Description about film №5",
                LocalDate.of(2005, 9, 11), 106,
                Set.of(new Genre(4, "Триллер"),
                        new Genre(6, "Боевик")),
                new MPA(4, "R", 17), new HashSet<>());

        filmStorage.createFilm(film1);
        filmStorage.createFilm(film2);
        filmStorage.createFilm(film3);
        filmStorage.createFilm(film4);
        filmStorage.createFilm(film5);

        User user1 = new User(1, "user@email.ru", "vanya123", "Ivan Petrov",
                LocalDate.of(1990, 1, 1), new HashSet<>());
        User user2 = new User(2, "user2@email.ru", "vasya321", "Vasya Ivanov",
                LocalDate.of(1992, 2, 2), new HashSet<>());
        User user3 = new User(3, "user3@email.ru", "bogdan_ultra", "Bogdan Zhukov",
                LocalDate.of(1993, 3, 3), new HashSet<>());
        User user4 = new User(4, "user4@email.ru", "chizhik", "Eugene Kulakov",
                LocalDate.of(1994, 4, 4), new HashSet<>());
        User user5 = new User(5, "user5@email.ru", "lovec_snov", "Gregory Chimushin",
                LocalDate.of(1995, 5, 5), new HashSet<>());

        userStorage.createUser(user1);
        userStorage.createUser(user2);
        userStorage.createUser(user3);
        userStorage.createUser(user4);
        userStorage.createUser(user5);

        filmStorage.addLikeToFilm(1, 1);
        filmStorage.addLikeToFilm(1, 2);
        Film likedFilm1 = filmStorage.addLikeToFilm(1, 3);
        filmStorage.addLikeToFilm(2, 3);
        Film likedFilm2 = filmStorage.addLikeToFilm(2, 5);
        filmStorage.addLikeToFilm(3, 1);
        filmStorage.addLikeToFilm(3, 2);
        filmStorage.addLikeToFilm(3, 3);
        filmStorage.addLikeToFilm(3, 4);
        Film likedFilm3 = filmStorage.addLikeToFilm(3, 5);
        filmStorage.addLikeToFilm(4, 2);
        Film likedFilm4 = filmStorage.addLikeToFilm(4, 4);
        filmStorage.addLikeToFilm(5, 2);
        filmStorage.addLikeToFilm(5, 3);
        Film likedFilm5 = filmStorage.addLikeToFilm(5, 5);

        List<Film> popularFilms = filmStorage.getPopularFilms(3);

        assertThat(popularFilms)
                .isNotNull()
                .isNotEmpty()
                .hasSameSizeAs(List.of(likedFilm3, likedFilm1, likedFilm5))
                .hasSize(3)
                .contains(likedFilm3, likedFilm1, likedFilm5);
    }

    @Test
    void getAllGenres() {
        List<Genre> allGenres = filmStorage.getAllGenres();
        assertThat(allGenres)
                .isNotNull()
                .isNotEmpty()
                .hasSameSizeAs(List.of(new Genre(1, "Комедия"),
                        new Genre(2, "Драма"),
                        new Genre(3, "Мультфильм"),
                        new Genre(4, "Триллер"),
                        new Genre(5, "Документальный"),
                        new Genre(6, "Боевик")))
                .hasSize(6)
                .contains(new Genre(1, "Комедия"),
                        new Genre(2, "Драма"),
                        new Genre(3, "Мультфильм"),
                        new Genre(4, "Триллер"),
                        new Genre(5, "Документальный"),
                        new Genre(6, "Боевик"));
    }

    @Test
    void getGenreById() {
        Genre foundGenre = filmStorage.getGenreById(3);
        assertThat(foundGenre)
                .isNotNull()
                .matches(genre -> genre.getName().equals("Мультфильм"))
                .usingRecursiveComparison()
                .isEqualTo(new Genre(3, "Мультфильм"))
                .hasSameHashCodeAs(new Genre(3, "Мультфильм"));
    }

    @Test
    void getAllMPA() {
        List<MPA> allGenres = filmStorage.getAllMPA();
        assertThat(allGenres)
                .isNotNull()
                .isNotEmpty()
                .hasSameSizeAs(List.of(
                        new MPA(1, "G", 0),
                        new MPA(2, "PG", 0),
                        new MPA(3, "PG-13", 13),
                        new MPA(4, "R", 17),
                        new MPA(5, "NC-17", 18)))
                .hasSize(5)
                .contains(new MPA(1, "G", 0),
                        new MPA(2, "PG", 0),
                        new MPA(3, "PG-13", 13),
                        new MPA(4, "R", 17),
                        new MPA(5, "NC-17", 18));
    }

    @Test
    void getMPAById() {
        MPA foundMPA = filmStorage.getMPAById(5);
        assertThat(foundMPA)
                .isNotNull()
                .matches(genre -> genre.getName().equals("NC-17"))
                .usingRecursiveComparison()
                .isEqualTo(new MPA(5, "NC-17", 18))
                .hasSameHashCodeAs(new MPA(5, "NC-17", 18));
    }
}