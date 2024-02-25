package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    private FilmService filmService;
    private Film film1;
    private Film film2;
    private Film film3;
    private Film film4;
    private Film film5;
    private Film film6;
    private Film film7;
    private Film film8;
    private Film film9;
    private Film film10;
    private Film film11;
    private Film film12;
    private User user1;
    private User user2;
    private User user3;
    private User user4;
    private User user5;
    private User user6;

    @BeforeEach
    void setUp() {
        filmService = new FilmService(new InMemoryFilmStorage(), new InMemoryUserStorage());
    }

    @AfterEach
    void clean() {
        filmService.deleteAllFilms();
        Film.resetCount();
        User.resetCount();
    }

    @Test
    void createFilmValid() {
        film1 = new Film(1, "Защитник",
                "биографический фильм режиссёра Питера Ландесмана с Уиллом Смитом",
                LocalDate.of(2015, 11, 10), 123);
        filmService.validate(film1, "добавить");
    }

    @Test
    void createFilmInvalid() {
        final Film film = new Film();
        Exception exc = assertThrows(ValidationException.class, () -> filmService.validate(film,
                "добавить"));
        assertEquals("Не удалось добавить фильм, т.к. наименование не заполнено", exc.getMessage());

        film.setName("");
        exc = assertThrows(ValidationException.class, () -> filmService.validate(film, "добавить"));
        assertEquals("Не удалось добавить фильм, т.к. наименование не заполнено", exc.getMessage());

        film.setName("Хоббит");
        film.setDescription("x".repeat(201));
        exc = assertThrows(ValidationException.class, () -> filmService.validate(film, "добавить"));
        assertEquals("Не удалось добавить фильм, т.к. максимальная длина описания 200 символов.",
                exc.getMessage());

        film.setDescription("x".repeat(100));
        film.setReleaseDate(LocalDate.of(1888, 12, 12));
        exc = assertThrows(ValidationException.class, () -> filmService.validate(film, "добавить"));
        assertEquals("Не удалось добавить фильм, " +
                        "т.к. дата релиза не может быть раньше даты рождения кино.", exc.getMessage());

        film.setReleaseDate(LocalDate.of(1988, 12, 12));
        film.setDuration(-1L);
        exc = assertThrows(ValidationException.class, () -> filmService.validate(film, "добавить"));
        assertEquals("Не удалось добавить фильм, " +
                "т.к. продолжительность фильма должна быть положительной.", exc.getMessage());
    }

    @Test
    void getFilmByIdValid() {
        film1 = new Film(1, "Защитник",
                "биографический фильм режиссёра Питера Ландесмана с Уиллом Смитом",
                LocalDate.of(2015, 11, 10), 123);
        filmService.createFilm(film1);
        final Film otherFilm = filmService.getFilmById(1);
        assertNotNull(otherFilm);
    }

    @Test
    void getFilmByIdInvalid() {
        film1 = new Film(1, "Защитник",
                "биографический фильм режиссёра Питера Ландесмана с Уиллом Смитом",
                LocalDate.of(2015, 11, 10), 123);
        filmService.createFilm(film1);

        Exception exc = assertThrows(NotFoundException.class, () -> filmService.getFilmById(2));
        assertEquals("Фильм с id: 2 не найден", exc.getMessage());

        final List<Film> filmList = filmService.getFilms();
        assertNotEquals(filmList.size(), 2);

        film2 = new Film(2, "Начало",
                "Запутанный фильм Кристофера Нолана",
                LocalDate.of(2010, 7, 8), 148);
        assertFalse(filmList.contains(film2));
    }

    @Test
    void getFilms() {
        assertEquals(filmService.getFilms().size(), 0);
        film1 = new Film(1, "Защитник",
                "биографический фильм режиссёра Питера Ландесмана с Уиллом Смитом",
                LocalDate.of(2015, 11, 10), 123);
        filmService.createFilm(film1);
        assertEquals(filmService.getFilms().size(), 1);
    }

    @Test
    void updateFilmValid() {
        film1 = new Film(1, "Защитник",
                "биографический фильм режиссёра Питера Ландесмана с Уиллом Смитом",
                LocalDate.of(2015, 11, 10), 123);
        filmService.createFilm(film1);
        film1.setDescription("Новое описание фильма Защитник");
        filmService.updateFilm(film1);
        assertEquals("Новое описание фильма Защитник", film1.getDescription());
    }

    @Test
    void updateFilmInvalid() {
        final Film film = new Film();
        Exception exc = assertThrows(ValidationException.class, () -> filmService.validate(film, "обновить"));
        assertEquals("Не удалось обновить фильм, т.к. наименование не заполнено", exc.getMessage());

        film.setName("");
        exc = assertThrows(ValidationException.class, () -> filmService.validate(film, "обновить"));
        assertEquals("Не удалось обновить фильм, т.к. наименование не заполнено", exc.getMessage());

        film.setName("Хоббит");
        film.setDescription("x".repeat(201));
        exc = assertThrows(ValidationException.class, () -> filmService.validate(film, "обновить"));
        assertEquals("Не удалось обновить фильм, т.к. максимальная длина описания 200 символов.",
                exc.getMessage());

        film.setDescription("x".repeat(100));
        film.setReleaseDate(LocalDate.of(1888, 12, 12));
        exc = assertThrows(ValidationException.class, () -> filmService.validate(film, "обновить"));
        assertEquals("Не удалось обновить фильм, " +
                "т.к. дата релиза не может быть раньше даты рождения кино.", exc.getMessage());

        film.setReleaseDate(LocalDate.of(1988, 12, 12));
        film.setDuration(-1L);
        exc = assertThrows(ValidationException.class, () -> filmService.validate(film, "обновить"));
        assertEquals("Не удалось обновить фильм, " +
                "т.к. продолжительность фильма должна быть положительной.", exc.getMessage());
    }

    @Test
    void deleteFilmByIdValid() {
        film1 = new Film(1, "Защитник",
                "биографический фильм режиссёра Питера Ландесмана с Уиллом Смитом",
                LocalDate.of(2015, 11, 10), 123);
        filmService.createFilm(film1);
        Map<String, String> response = filmService.deleteFilmById(1);
        assertEquals(response, Map.of("info", String.format("Фильм по id: 1 успешно удален")));
        assertEquals(filmService.getFilms().size(), 0);
    }

    @Test
    void deleteFilmByIdInvalid() {
        Exception exc = assertThrows(NotFoundException.class, () -> filmService.getFilmById(1));
        assertEquals("Фильм с id: 1 не найден", exc.getMessage());
        assertFalse(filmService.getFilms().remove(film1));
    }

    @Test
    void deleteAllFilms() {
        film1 = new Film(1, "Защитник",
                "биографический фильм режиссёра Питера Ландесмана с Уиллом Смитом",
                LocalDate.of(2015, 11, 10), 123);
        film2 = new Film(2, "Начало",
                "Запутанный фильм Кристофера Нолана",
                LocalDate.of(2010, 7, 8), 148);
        film3 = new Film(3, "Зеленая миля",
                "Пол Эджкомб — начальник блока смертников в тюрьме «Холодная гора», каждый из узников " +
                        "которого однажды проходит «зеленую милю» по пути к месту казни. Пол повидал много " +
                        "заключённых и надзирателей за время работы. Однако гигант Джон Коффи, обвинённый в " +
                        "страшном преступлении, стал одним из самых необычных обитателей блока.",
                LocalDate.of(1999, 12, 6), 189);
        filmService.createFilm(film1);
        filmService.createFilm(film2);
        filmService.createFilm(film3);
        Map<String, String> response = filmService.deleteAllFilms();
        assertEquals(response, Map.of("info", String.format("Все фильмы успешно удалены")));
        assertEquals(filmService.getFilms().size(), 0);
    }

    @Test
    void addLikeToFilm() {
        film1 = new Film(1, "Защитник",
                "биографический фильм режиссёра Питера Ландесмана с Уиллом Смитом",
                LocalDate.of(2015, 11, 10), 123);
        film2 = new Film(2, "Начало",
                "Запутанный фильм Кристофера Нолана",
                LocalDate.of(2010, 7, 8), 148);
        filmService.createFilm(film1);
        filmService.createFilm(film2);

        user1 = new User(1,"test@test.ru",
                "testLogin", "Test-name",
                LocalDate.of(2015, 11, 10));
        user2 = new User(2,"test2@test2.ru",
                "testLogin2", "Test-name2",
                LocalDate.of(1994, 5, 12));
        filmService.getUserStorage().createUser(user1);
        filmService.getUserStorage().createUser(user2);

        filmService.addLikeToFilm(1, 1);
        filmService.addLikeToFilm(1, 2);
        assertEquals(film1.getLikes().size(), 2);
        assertFalse(film1.getLikes().isEmpty());
        assertEquals(film1.getLikes(), Set.of(1, 2));
    }

    @Test
    void deleteLikeFromFilm() {
        film1 = new Film(1, "Защитник",
                "биографический фильм режиссёра Питера Ландесмана с Уиллом Смитом",
                LocalDate.of(2015, 11, 10), 123);
        film2 = new Film(2, "Начало",
                "Запутанный фильм Кристофера Нолана",
                LocalDate.of(2010, 7, 8), 148);
        filmService.createFilm(film1);
        filmService.createFilm(film2);

        user1 = new User(1,"test@test.ru",
                "testLogin", "Test-name",
                LocalDate.of(2015, 11, 10));
        user2 = new User(2,"test2@test2.ru",
                "testLogin2", "Test-name2",
                LocalDate.of(1994, 5, 12));
        filmService.getUserStorage().createUser(user1);
        filmService.getUserStorage().createUser(user2);

        filmService.addLikeToFilm(1, 1);
        filmService.addLikeToFilm(1, 2);
        filmService.deleteLikeFromFilm(1, 1);
        assertEquals(film1.getLikes().size(), 1);
        assertFalse(film1.getLikes().isEmpty());
        assertEquals(film1.getLikes(), Set.of(2));
    }

    @Test
    void getPopularFilms() {
        film1 = new Film(1, "Защитник",
                "биографический фильм режиссёра Питера Ландесмана с Уиллом Смитом",
                LocalDate.of(2015, 11, 10), 123);
        film2 = new Film(2, "Начало",
                "Запутанный фильм Кристофера Нолана",
                LocalDate.of(2010, 7, 8), 148);
        film3 = new Film(3, "Зеленая миля",
                "Пол Эджкомб — начальник блока смертников в тюрьме «Холодная гора», каждый из узников " +
                        "которого однажды проходит «зеленую милю» по пути к месту казни. Пол повидал много " +
                        "заключённых и надзирателей за время работы. Однако гигант Джон Коффи, обвинённый в " +
                        "страшном преступлении, стал одним из самых необычных обитателей блока.",
                LocalDate.of(1999, 12, 6), 189);
        film4 = new Film(4, "1+1",
                "Аристократ на коляске нанимает в сиделки бывшего заключенного. " +
                        "Искрометная французская комедия с Омаром Си",
                LocalDate.of(2011, 9, 23), 112);
        film5 = new Film(5, "Побег из Шоушенка",
                "Бухгалтер Энди Дюфрейн обвинён в убийстве собственной жены и её любовника. Оказавшись " +
                        "в тюрьме под названием Шоушенк, он сталкивается с жестокостью и беззаконием, царящими по " +
                        "обе стороны решётки. Каждый, кто попадает в эти стены, становится их рабом до конца жизни. " +
                        "Но Энди, обладающий живым умом и доброй душой, находит подход как к заключённым, так и " +
                        "к охранникам, добиваясь их особого к себе расположения.",
                LocalDate.of(1994, 9, 10), 142);
        film6 = new Film(6, "Форрест Гамп",
                "Сидя на автобусной остановке, Форрест Гамп — не очень умный, но добрый и открытый " +
                        "парень — рассказывает случайным встречным историю своей необыкновенной жизни.",
                LocalDate.of(1994, 6, 23), 142);
        film7 = new Film(7, "Интерстеллар",
                "Когда засуха, пыльные бури и вымирание растений приводят человечество к " +
                        "продовольственному кризису, коллектив исследователей и учёных отправляется сквозь " +
                        "червоточину (которая предположительно соединяет области пространства-времени через " +
                        "большое расстояние) в путешествие, чтобы превзойти прежние ограничения для космических " +
                        "путешествий человека и найти планету с подходящими для человечества условиями.",
                LocalDate.of(2014, 10, 26), 169);
        film8 = new Film(8, "Унесённые призраками",
                "Тихиро с мамой и папой переезжает в новый дом. Заблудившись по дороге, они оказываются " +
                        "в странном пустынном городе, где их ждет великолепный пир. Родители с жадностью " +
                        "набрасываются на еду и к ужасу девочки превращаются в свиней, став пленниками злой " +
                        "колдуньи Юбабы. Теперь, оказавшись одна среди волшебных существ и загадочных видений, " +
                        "Тихиро должна придумать, как избавить своих родителей от чар коварной старухи.",
                LocalDate.of(2001, 7, 20), 125);
        film9 = new Film(9, "Властелин колец: Возвращение короля",
                "Повелитель сил тьмы Саурон направляет свою бесчисленную армию под стены Минас-Тирита, " +
                        "крепости Последней Надежды. Он предвкушает близкую победу, но именно это мешает ему " +
                        "заметить две крохотные фигурки — хоббитов, приближающихся к Роковой Горе, где им предстоит " +
                        "уничтожить Кольцо Всевластья.",
                LocalDate.of(2003, 12, 1), 201);
        film10 = new Film(10, "Бойцовский клуб",
                "Страховой работник разрушает рутину своей благополучной жизни. Культовая драма " +
                        "по книге Чака Паланика",
                LocalDate.of(1999, 9, 10), 139);
        film11 = new Film(11, "Список Шиндлера",
                "Фильм рассказывает реальную историю загадочного Оскара Шиндлера, члена нацистской " +
                        "партии, преуспевающего фабриканта, спасшего во время Второй мировой войны почти 1200 евреев.",
                LocalDate.of(1993, 11, 30), 195);
        film12 = new Film(12, "Шрэк",
                "Жил да был в сказочном государстве большой зеленый великан по имени Шрэк. " +
                        "Жил он в гордом одиночестве в лесу, на болоте, которое считал своим. Но однажды " +
                        "злобный коротышка — лорд Фаркуад, правитель волшебного королевства, безжалостно " +
                        "согнал на Шрэково болото всех сказочных обитателей.",
                LocalDate.of(2001, 4, 22), 90);

        filmService.createFilm(film1);
        filmService.createFilm(film2);
        filmService.createFilm(film3);
        filmService.createFilm(film4);
        filmService.createFilm(film5);
        filmService.createFilm(film6);
        filmService.createFilm(film7);
        filmService.createFilm(film8);
        filmService.createFilm(film9);
        filmService.createFilm(film10);
        filmService.createFilm(film11);
        filmService.createFilm(film12);

        user1 = new User(1,"test@test.ru",
                "testLogin", "Test-name",
                LocalDate.of(2015, 11, 10));
        user2 = new User(2,"test2@test2.ru",
                "testLogin2", "Test-name2",
                LocalDate.of(1994, 5, 12));
        user3 = new User(3,"test3@test3.ru",
                "testLogin3", "Test-name3",
                LocalDate.of(1993, 1, 16));
        user4 = new User(4,"test4@test4.ru",
                "testLogin4", "Test-name4",
                LocalDate.of(1978, 7, 23));
        user5 = new User(5,"test5@test5.ru",
                "testLogin5", "Test-name5",
                LocalDate.of(1996, 11, 20));
        user6 = new User(6,"test6@test6.ru",
                "testLogin6", "Test-name6",
                LocalDate.of(1969, 6, 16));

        filmService.getUserStorage().createUser(user1);
        filmService.getUserStorage().createUser(user2);
        filmService.getUserStorage().createUser(user3);
        filmService.getUserStorage().createUser(user4);
        filmService.getUserStorage().createUser(user5);
        filmService.getUserStorage().createUser(user6);

        filmService.addLikeToFilm(1, 1);
        filmService.addLikeToFilm(1, 2);
        filmService.addLikeToFilm(1, 3);
        filmService.addLikeToFilm(1, 4);
        filmService.addLikeToFilm(1, 5);
        filmService.addLikeToFilm(1, 6);

        filmService.addLikeToFilm(3, 1);
        filmService.addLikeToFilm(3, 3);
        filmService.addLikeToFilm(3, 5);

        filmService.addLikeToFilm(5, 1);
        filmService.addLikeToFilm(5, 2);
        filmService.addLikeToFilm(5, 3);
        filmService.addLikeToFilm(5, 4);
        filmService.addLikeToFilm(5, 5);

        filmService.addLikeToFilm(7, 1);
        filmService.addLikeToFilm(7, 2);
        filmService.addLikeToFilm(7, 3);
        filmService.addLikeToFilm(7, 4);

        filmService.addLikeToFilm(11, 1);

        assertEquals(List.of(film1, film5), filmService.getPopularFilms(2));
        assertEquals(List.of(film1, film5, film7, film3, film11),
                filmService.getPopularFilms(5));
    }
}